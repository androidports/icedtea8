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

import java.security.AccessController;
import java.security.PrivilegedAction;
import sun.security.action.GetPropertyAction;

import org.classpath.icedtea.java.nio.file.spi.FileSystemProvider;

/**
 * Creates this platform's default FileSystemProvider.
 */

public class DefaultFileSystemProvider {
    private DefaultFileSystemProvider() { }

    @SuppressWarnings("unchecked")
    private static FileSystemProvider createProvider(final String cn) {
        return AccessController
            .doPrivileged(new PrivilegedAction<FileSystemProvider>() {
                public FileSystemProvider run() {
                    Class<FileSystemProvider> c;
                    try {
                        c = (Class<FileSystemProvider>)Class.forName(cn, true, null);
                    } catch (ClassNotFoundException x) {
                        throw new AssertionError(x);
                    }
                    try {
                        return c.newInstance();
                    } catch (IllegalAccessException x) {
                        throw new AssertionError(x);
                    } catch (InstantiationException x) {
                        throw new AssertionError(x);
                    }
            }});
    }

    /**
     * Returns the default FileSystemProvider.
     */
    public static FileSystemProvider create() {
        String osname = AccessController
            .doPrivileged(new GetPropertyAction("os.name"));
        if (osname.equals("SunOS"))
            return createProvider("sun.nio.fs.SolarisFileSystemProvider");
        if (osname.equals("Linux"))
            return createProvider("sun.nio.fs.LinuxFileSystemProvider");
        throw new AssertionError("Platform not recognized");
    }
}
