/*
 * Copyright 2007-2008 Sun Microsystems, Inc.  All Rights Reserved.
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

import java.io.IOException;
import java.util.*;
import java.util.regex.Pattern;
import java.security.PrivilegedAction;
import java.security.AccessController;
import sun.security.action.GetBooleanAction;

import java.nio.file.FileStore;
import java.nio.file.FileSystem;
import java.nio.file.LinkOption;
import java.nio.file.Path;
import java.nio.file.PathMatcher;

import java.nio.file.attribute.BasicFileAttributeView;
import java.nio.file.attribute.FileAttributeView;
import java.nio.file.attribute.FileOwnerAttributeView;
import java.nio.file.attribute.PosixFileAttributeView;
import java.nio.file.attribute.GroupPrincipal;
import java.nio.file.attribute.UserPrincipal;
import java.nio.file.attribute.UserPrincipalLookupService;

import java.nio.file.spi.FileSystemProvider;

/**
 * Base implementation of FileSystem for Unix-like implementations.
 */

abstract class UnixFileSystem
    extends FileSystem
{
    private final UnixFileSystemProvider provider;
    private final byte[] defaultDirectory;
    private final boolean needToResolveAgainstDefaultDirectory;
    private final UnixPath rootDirectory;

    // package-private
    UnixFileSystem(UnixFileSystemProvider provider, String dir) {
        this.provider = provider;
        this.defaultDirectory = UnixPath.normalizeAndCheck(dir).getBytes();
        if (this.defaultDirectory[0] != '/') {
            throw new RuntimeException("default directory must be absolute");
        }

        // if process-wide chdir is allowed or default directory is not the
        // process working directory then paths must be resolved against the
        // default directory.
        PrivilegedAction<Boolean> pa = new GetBooleanAction("sun.nio.fs.chdirAllowed");
        if (AccessController.doPrivileged(pa).booleanValue()) {
            this.needToResolveAgainstDefaultDirectory = true;
        } else {
            byte[] cwd = UnixNativeDispatcher.getcwd();
            boolean defaultIsCwd = (cwd.length == defaultDirectory.length);
            if (defaultIsCwd) {
                for (int i=0; i<cwd.length; i++) {
                    if (cwd[i] != defaultDirectory[i]) {
                        defaultIsCwd = false;
                        break;
                    }
                }
            }
            this.needToResolveAgainstDefaultDirectory = !defaultIsCwd;
        }

        // the root directory
        this.rootDirectory = new UnixPath(this, "/");
    }

    // package-private
    byte[] defaultDirectory() {
        return defaultDirectory;
    }

    boolean needToResolveAgainstDefaultDirectory() {
        return needToResolveAgainstDefaultDirectory;
    }

    UnixPath rootDirectory() {
        return rootDirectory;
    }

    boolean isSolaris() {
        return false;
    }


    public final FileSystemProvider provider() {
        return provider;
    }


    public final String getSeparator() {
        return "/";
    }


    public final boolean isOpen() {
        return true;
    }


    public final boolean isReadOnly() {
        return false;
    }


    public final void close() throws IOException {
        throw new UnsupportedOperationException();
    }

    /**
     * Copies non-POSIX attributes from the source to target file.
     *
     * Copying a file preserving attributes, or moving a file, will preserve
     * the file owner/group/permissions/timestamps but it does not preserve
     * other non-POSIX attributes. This method is invoked by the
     * copy or move operation to preserve these attributes. It should copy
     * extended attributes, ACLs, or other attributes.
     *
     * @param   sfd
     *          Open file descriptor to source file
     * @param   tfd
     *          Open file descriptor to target file
     */
    abstract void copyNonPosixAttributes(int sfd, int tfd);

    /**
     * Tells if directory relative system calls (openat, etc.) are available
     * on this operating system.
     */
    abstract boolean supportsSecureDirectoryStreams();

    /**
     * Unix systems only have a single root directory (/)
     */

    public final Iterable<Path> getRootDirectories() {
        final List<Path> allowedList =
           Collections.unmodifiableList(Arrays.asList((Path)rootDirectory));
        return new Iterable<Path>() {
            public Iterator<Path> iterator() {
                try {
                    SecurityManager sm = System.getSecurityManager();
                    if (sm != null)
                        sm.checkRead(rootDirectory.toString());
                    return allowedList.iterator();
                } catch (SecurityException x) {
                    List<Path> disallowed = Collections.emptyList();
                    return disallowed.iterator();
                }
            }
        };
    }

    /**
     * Returns object to iterate over entries in mounttab or equivalent
     */
    abstract Iterable<UnixMountEntry> getMountEntries();

    /**
     * Returns a FileStore to represent the file system where the given file
     * reside.
     */
    abstract FileStore getFileStore(UnixPath path) throws IOException;

    /**
     * Returns a FileStore to represent the file system for the given mount
     * mount.
     */
    abstract FileStore getFileStore(UnixMountEntry entry);

    /**
     * Iterator returned by getFileStores method.
     */
    private class FileStoreIterator implements Iterator<FileStore> {
        private final Iterator<UnixMountEntry> entries;
        private FileStore next;

        FileStoreIterator() {
            this.entries = getMountEntries().iterator();
        }

        private FileStore readNext() {
            assert Thread.holdsLock(this);
            for (;;) {
                if (!entries.hasNext())
                    return null;
                UnixMountEntry entry = entries.next();

                // skip entries with the "ignore" option
                if (entry.isIgnored())
                    continue;

                // check permission to read mount point
                SecurityManager sm = System.getSecurityManager();
                if (sm != null) {
                    try {
                        sm.checkRead(new String(entry.dir()));
                    } catch (SecurityException x) {
                        continue;
                    }
                }
                return getFileStore(entry);
            }
        }


        public synchronized boolean hasNext() {
            if (next != null)
                return true;
            next = readNext();
            return next != null;
        }


        public synchronized FileStore next() {
            if (next == null)
                next = readNext();
            if (next == null) {
                throw new NoSuchElementException();
            } else {
                FileStore result = next;
                next = null;
                return result;
            }
        }


        public void remove() {
            throw new UnsupportedOperationException();
        }
    }


    public final Iterable<FileStore> getFileStores() {
        return new Iterable<FileStore>() {
            public Iterator<FileStore> iterator() {
                return new FileStoreIterator();
            }
        };
    }


    public final UnixPath getPath(String path) {
        return new UnixPath(this, path);
    }


    public PathMatcher getNameMatcher(String syntax, String input) {
        String expr;
        if (syntax.equals(GLOB_SYNTAX)) {
            expr = Globs.toRegexPattern(input);
        } else {
            if (syntax.equals(REGEX_SYNTAX)) {
                expr = input;
            } else {
                throw new UnsupportedOperationException("Syntax '" + syntax +
                    "' not recognized");
            }
        }

        // return matcher
        final Pattern pattern = Pattern.compile(expr);
        return new PathMatcher() {

            public boolean matches(Path path) {
                // match on file name only
                Path name = path.getName();
                if (name == null)
                    return false;
                return pattern.matcher(name.toString()).matches();
            }
        };
    }
    private static final String GLOB_SYNTAX = "glob";
    private static final String REGEX_SYNTAX = "regex";

    protected boolean followLinks(LinkOption... options) {
        boolean followLinks = true;
        for (LinkOption option: options) {
            if (option == LinkOption.NOFOLLOW_LINKS) {
                followLinks = false;
                continue;
            }
            throw new AssertionError("Should not get here");
        }
        return followLinks;
    }

    @SuppressWarnings("unchecked")
    protected <V extends FileAttributeView> V newFileAttributeView(Class<V> view,
                                                                   UnixPath file,
                                                                   LinkOption... options)
    {
        if (view == null)
            throw new NullPointerException();
        boolean followLinks = followLinks(options);
        Class<?> c = view;
        if (c == BasicFileAttributeView.class)
            return (V) UnixFileAttributeViews.createBasicView(file, followLinks);
        if (c == PosixFileAttributeView.class)
            return (V) UnixFileAttributeViews.createPosixView(file, followLinks);
        if (c == FileOwnerAttributeView.class)
            return (V) UnixFileAttributeViews.createOwnerView(file, followLinks);
        return (V) null;
    }

    static List<String> standardFileAttributeViews() {
        return Arrays.asList("basic", "posix", "unix", "owner");
    }

    protected FileAttributeView newFileAttributeView(String name,
                                                     UnixPath file,
                                                     LinkOption... options)
    {
        boolean followLinks = followLinks(options);
        if (name.equals("basic"))
            return UnixFileAttributeViews.createBasicView(file, followLinks);
        if (name.equals("posix"))
            return UnixFileAttributeViews.createPosixView(file, followLinks);
        if (name.equals("unix"))
            return UnixFileAttributeViews.createUnixView(file, followLinks);
        if (name.equals("owner"))
            return UnixFileAttributeViews.createOwnerView(file, followLinks);
        return null;
    }


    public final UserPrincipalLookupService getUserPrincipalLookupService() {
        return theLookupService;
    }

    private static final UserPrincipalLookupService theLookupService =
        new UserPrincipalLookupService() {

            public UserPrincipal lookupPrincipalByName(String name)
                throws IOException
            {
                return UnixUserPrincipals.lookupUser(name);
            }


            public GroupPrincipal lookupPrincipalByGroupName(String group)
                throws IOException
            {
                return UnixUserPrincipals.lookupGroup(group);
            }
        };
}
