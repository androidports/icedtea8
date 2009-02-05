/*
 * Copyright 2007 Sun Microsystems, Inc.  All Rights Reserved.
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS FILE HEADER.
 *
 * This code is free software; you can redistribute it and/or modify it
 * under the terms of the GNU General Public License version 2 only, as
 * published by the Free Software Foundation.  Sun designates this
 * particular file as subject to the "Classpath" exception as provided
 * by Sun in the LICENSE file that accompanied this code.
 *
 * This code is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or
 * FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License
 * version 2 for more details (a copy is included in the LICENSE file that
 * accompanied this code).
 *
 * You should have received a copy of the GNU General Public License version
 * 2 along with this work; if not, write to the Free Software Foundation,
 * Inc., 51 Franklin St, Fifth Floor, Boston, MA 02110-1301 USA.
 *
 * Please contact Sun Microsystems, Inc., 4150 Network Circle, Santa Clara,
 * CA 95054 USA or visit www.sun.com if you need additional information or
 * have any questions.
 */

package sun.nio.fs;

import java.nio.file.*;
import java.nio.channels.*;
import java.io.FileDescriptor;
import java.io.IOException;
import java.util.*;

import com.sun.nio.file.ExtendedOpenOption;

import sun.nio.ch.FileChannelImpl;
import sun.nio.ch.ThreadPool;
import sun.nio.ch.WindowsAsynchronousFileChannelImpl;
import sun.misc.SharedSecrets;
import sun.misc.JavaIOFileDescriptorAccess;

import static sun.nio.fs.WindowsNativeDispatcher.*;
import static sun.nio.fs.WindowsConstants.*;

/**
 * Factory to create FileChannels and AsynchronousFileChannels.
 */

class WindowsChannelFactory {
    private static final JavaIOFileDescriptorAccess fdAccess =
        SharedSecrets.getJavaIOFileDescriptorAccess();

    private WindowsChannelFactory() { }

    /**
     * Do not follow reparse points when opening an existing file. Do not fail
     * if the file is a reparse point.
     */
    static final OpenOption OPEN_REPARSE_POINT = new OpenOption() { };

    /**
     * Represents the flags from a user-supplied set of open options.
     */
    private static class Flags {
        boolean read;
        boolean write;
        boolean append;
        boolean truncateExisting;
        boolean create;
        boolean createNew;
        boolean deleteOnClose;
        boolean sparse;
        boolean overlapped;
        boolean sync;
        boolean dsync;

        // non-standard
        boolean shareRead = true;
        boolean shareWrite = true;
        boolean shareDelete = true;
        boolean noFollowLinks;
        boolean openReparsePoint;

        static Flags toFlags(Set<? extends OpenOption> options) {
            Flags flags = new Flags();
            for (OpenOption option: options) {
                if (!(option instanceof StandardOpenOption)) {
                    if (option == ExtendedOpenOption.NOSHARE_READ) {
                        flags.shareRead = false;
                        continue;
                    }
                    if (option == ExtendedOpenOption.NOSHARE_WRITE) {
                        flags.shareWrite = false;
                        continue;
                    }
                    if (option == ExtendedOpenOption.NOSHARE_DELETE) {
                        flags.shareDelete = false;
                        continue;
                    }
                    if (option == LinkOption.NOFOLLOW_LINKS) {
                        flags.noFollowLinks = true;
                        continue;
                    }
                    if (option == OPEN_REPARSE_POINT) {
                        flags.openReparsePoint = true;
                        continue;
                    }
                    if (option == null)
                        throw new NullPointerException();
                    throw new UnsupportedOperationException("Unsupported open option");
                }
                switch ((StandardOpenOption)option) {
                    case READ : flags.read = true; break;
                    case WRITE : flags.write = true; break;
                    case APPEND : flags.append = true; break;
                    case TRUNCATE_EXISTING : flags.truncateExisting = true; break;
                    case CREATE : flags.create = true; break;
                    case CREATE_NEW : flags.createNew = true; break;
                    case DELETE_ON_CLOSE : flags.deleteOnClose = true; break;
                    case SPARSE : flags.sparse = true; break;
                    case SYNC : flags.sync = true; break;
                    case DSYNC : flags.dsync = true; break;
                    default: throw new AssertionError("Should not get here");
                }
            }
            return flags;
        }
    }

    /**
     * Open/creates file, returning FileChannel to access the file
     *
     * @param   pathForWindows
     *          The path of the file to open/create
     * @param   pathToCheck
     *          The path used for permission checks (if security manager)
     */
    static FileChannel newFileChannel(String pathForWindows,
                                      String pathToCheck,
                                      Set<? extends OpenOption> options,
                                      long pSecurityDescriptor)
        throws WindowsException
    {
        Flags flags = Flags.toFlags(options);

        // default is reading; append => writing
        if (!flags.read && !flags.write) {
            if (flags.append) {
                flags.write = true;
            } else {
                flags.read = true;
            }
        }

        // validation
        if (flags.read && flags.append)
            throw new IllegalArgumentException("READ + APPEND not allowed");
        if (flags.append && flags.truncateExisting)
            throw new IllegalArgumentException("APPEND + TRUNCATE_EXISTING not allowed");

        FileDescriptor fdObj = open(pathForWindows, pathToCheck, flags, pSecurityDescriptor);
        return FileChannelImpl.open(fdObj, flags.read, flags.write, null);
    }

    /**
     * Open/creates file, returning AsynchronousFileChannel to access the file
     *
     * @param   pathForWindows
     *          The path of the file to open/create
     * @param   pathToCheck
     *          The path used for permission checks (if security manager)
     * @param   pool
     *          The thread pool that the channel is associated with
     */
    static AsynchronousFileChannel newAsynchronousFileChannel(String pathForWindows,
                                                              String pathToCheck,
                                                              Set<? extends OpenOption> options,
                                                              long pSecurityDescriptor,
                                                              ThreadPool pool)
        throws IOException
    {
        Flags flags = Flags.toFlags(options);

        // Overlapped I/O required
        flags.overlapped = true;

        // default is reading
        if (!flags.read && !flags.write) {
            flags.read = true;
        }

        // validation
        if (flags.append)
            throw new UnsupportedOperationException("APPEND not allowed");

        // open file for overlapped I/O
        FileDescriptor fdObj;
        try {
            fdObj = open(pathForWindows, pathToCheck, flags, pSecurityDescriptor);
        } catch (WindowsException x) {
            x.rethrowAsIOException(pathForWindows);
            return null;
        }

        // create the AsynchronousFileChannel
        try {
            return WindowsAsynchronousFileChannelImpl.open(fdObj, flags.read, flags.write, pool);
        } catch (IOException x) {
            // IOException is thrown if the file handle cannot be associated
            // with the completion port. All we can do is close the file.
            long handle = fdAccess.getHandle(fdObj);
            CloseHandle(handle);
            throw x;
        }
    }

    /**
     * Opens file based on parameters and options, returning a FileDescriptor
     * encapsulating the handle to the open file.
     */
    private static FileDescriptor open(String pathForWindows,
                                       String pathToCheck,
                                       Flags flags,
                                       long pSecurityDescriptor)
        throws WindowsException
    {
        // set to true if file must be truncated after open
        boolean truncateAfterOpen = false;

        // map options
        int dwDesiredAccess = 0;
        if (flags.read)
            dwDesiredAccess |= GENERIC_READ;
        if (flags.write)
            dwDesiredAccess |= (flags.append) ? FILE_APPEND_DATA : GENERIC_WRITE;

        int dwShareMode = 0;
        if (flags.shareRead)
            dwShareMode |= FILE_SHARE_READ;
        if (flags.shareWrite)
            dwShareMode |= FILE_SHARE_WRITE;
        if (flags.shareDelete)
            dwShareMode |= FILE_SHARE_DELETE;

        int dwFlagsAndAttributes = FILE_ATTRIBUTE_NORMAL;
        int dwCreationDisposition = OPEN_EXISTING;
        if (flags.write) {
            if (flags.createNew) {
                dwCreationDisposition = CREATE_NEW;
                // force create to fail if file is orphaned reparse point
                dwFlagsAndAttributes |= FILE_FLAG_OPEN_REPARSE_POINT;
            } else {
                if (flags.create)
                    dwCreationDisposition = OPEN_ALWAYS;
                if (flags.truncateExisting) {
                    // Windows doesn't have a creation disposition that exactly
                    // corresponds to CREATE + TRUNCATE_EXISTING so we use
                    // the OPEN_ALWAYS mode and then truncate the file.
                    if (dwCreationDisposition == OPEN_ALWAYS) {
                        truncateAfterOpen = true;
                    } else {
                        dwCreationDisposition = TRUNCATE_EXISTING;
                    }
                }
            }
        }

        if (flags.dsync || flags.sync)
            dwFlagsAndAttributes |= FILE_FLAG_WRITE_THROUGH;
        if (flags.overlapped)
            dwFlagsAndAttributes |= FILE_FLAG_OVERLAPPED;
        if (flags.deleteOnClose)
            dwFlagsAndAttributes |= FILE_FLAG_DELETE_ON_CLOSE;

        // NOFOLLOW_LINKS and NOFOLLOW_REPARSEPOINT mean open reparse point
        boolean okayToFollowLinks = true;
        if (dwCreationDisposition != CREATE_NEW &&
            (flags.noFollowLinks ||
             flags.openReparsePoint ||
             flags.deleteOnClose))
        {
            if (flags.noFollowLinks)
                okayToFollowLinks = false;
            dwFlagsAndAttributes |= FILE_FLAG_OPEN_REPARSE_POINT;
        }

        // permission check
        if (pathToCheck != null) {
            SecurityManager sm = System.getSecurityManager();
            if (sm != null) {
                if (flags.read)
                    sm.checkRead(pathToCheck);
                if (flags.write)
                    sm.checkWrite(pathToCheck);
                if (flags.deleteOnClose)
                    sm.checkDelete(pathToCheck);
            }
        }

        // open file
        long handle = CreateFile(pathForWindows,
                                 dwDesiredAccess,
                                 dwShareMode,
                                 pSecurityDescriptor,
                                 dwCreationDisposition,
                                 dwFlagsAndAttributes);

        // make sure this isn't a symbolic link.
        if (!okayToFollowLinks) {
            try {
                // for security reasons we can only test here the file is a
                // reparse point. To read the reparse point tag would require
                // re-opening the file and this method is required to be atomic.
                if (WindowsFileAttributes.readAttributes(handle).isReparsePoint()) {
                    throw new WindowsException("File is reparse point");
                }

            } catch (WindowsException x) {
                CloseHandle(handle);
                throw x;
            }
        }

        // truncate file (for CREATE + TRUNCATE_EXISTING case)
        if (truncateAfterOpen) {
            try {
                SetEndOfFile(handle);
            } catch (WindowsException x) {
                CloseHandle(handle);
                throw x;
            }
        }

        // make the file sparse if needed
        if (dwCreationDisposition == CREATE_NEW && flags.sparse) {
            try {
                DeviceIoControlSetSparse(handle);
            } catch (WindowsException x) {
                // ignore as sparse option is hint
            }
        }

        // create FileDescriptor and return
        FileDescriptor fdObj = new FileDescriptor();
        fdAccess.setHandle(fdObj, handle);
        return fdObj;
    }
}
