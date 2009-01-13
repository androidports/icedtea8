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

import java.util.concurrent.TimeUnit;
import java.util.Set;
import java.util.HashSet;

import org.classpath.icedtea.java.nio.file.attribute.BasicFileAttributes;
import org.classpath.icedtea.java.nio.file.attribute.PosixFileAttributes;

/**
 * Unix implementation of PosixFileAttributes.
 */

class UnixFileAttributes
    implements PosixFileAttributes
{
    private int     st_mode;
    private long    st_ino;
    private long    st_dev;
    private long    st_rdev;
    private int     st_nlink;
    private int     st_uid;
    private int     st_gid;
    private long    st_size;
    private long    st_atime;
    private long    st_mtime;
    private long    st_ctime;

    // created lazily
    private volatile UserPrincipal owner;
    private volatile GroupPrincipal group;
    private volatile UnixFileKey key;

    private UnixFileAttributes() {
    }

    // get the UnixFileAttributes for a given file
    static UnixFileAttributes get(UnixPath path, boolean followLinks)
        throws UnixException
    {
        UnixFileAttributes attrs = new UnixFileAttributes();
        if (followLinks) {
            UnixNativeDispatcher.stat(path, attrs);
        } else {
            UnixNativeDispatcher.lstat(path, attrs);
        }
        return attrs;
    }

    // get the UnixFileAttributes for an open file
    static UnixFileAttributes get(int fd) throws UnixException {
        UnixFileAttributes attrs = new UnixFileAttributes();
        UnixNativeDispatcher.fstat(fd, attrs);
        return attrs;
    }

    // get the UnixFileAttributes for a given file, relative to open directory
    static UnixFileAttributes get(int dfd, UnixPath path, boolean followLinks)
        throws UnixException
    {
        UnixFileAttributes attrs = new UnixFileAttributes();
        int flag = (followLinks) ? 0 : UnixConstants.AT_SYMLINK_NOFOLLOW;
        UnixNativeDispatcher.fstatat(dfd, path.asByteArray(), flag, attrs);
        return attrs;
    }

    // package-private
    boolean isSameFile(UnixFileAttributes attrs) {
        return ((st_ino == attrs.st_ino) && (st_dev == attrs.st_dev));
    }

    // package-private
    int mode()  { return st_mode; }
    long ino()  { return st_ino; }
    long dev()  { return st_dev; }
    long rdev() { return st_rdev; }
    int uid()   { return st_uid; }
    int gid()   { return st_gid; }
    long ctime() { return st_ctime; }

    boolean isDevice() {
        int type = st_mode & UnixConstants.S_IFMT;
        return (type == UnixConstants.S_IFCHR ||
                type == UnixConstants.S_IFBLK  ||
                type == UnixConstants.S_IFIFO);
    }


    public long lastModifiedTime() {
        return st_mtime;
    }


    public long lastAccessTime() {
        return st_atime;
    }


    public long creationTime() {
        return -1L;
    }


    public TimeUnit resolution() {
        return TimeUnit.MILLISECONDS;
    }


    public boolean isRegularFile() {
       return ((st_mode & UnixConstants.S_IFMT) == UnixConstants.S_IFREG);
    }


    public boolean isDirectory() {
        return ((st_mode & UnixConstants.S_IFMT) == UnixConstants.S_IFDIR);
    }


    public boolean isSymbolicLink() {
        return ((st_mode & UnixConstants.S_IFMT) == UnixConstants.S_IFLNK);
    }


    public boolean isOther() {
        int type = st_mode & UnixConstants.S_IFMT;
        return (type != UnixConstants.S_IFREG &&
                type != UnixConstants.S_IFDIR &&
                type != UnixConstants.S_IFLNK);
    }


    public long size() {
        return st_size;
    }


    public int linkCount() {
        return st_nlink;
    }


    public UnixFileKey fileKey() {
        if (key == null) {
            synchronized (this) {
                if (key == null) {
                    key = new UnixFileKey(st_dev, st_ino);
                }
            }
        }
        return key;
    }


    public UserPrincipal owner() {
        if (owner == null) {
            synchronized (this) {
                if (owner == null) {
                    owner = UnixUserPrincipals.fromUid(st_uid);
                }
            }
        }
        return owner;
    }


    public GroupPrincipal group() {
        if (group == null) {
            synchronized (this) {
                if (group == null) {
                    group = UnixUserPrincipals.fromGid(st_gid);
                }
            }
        }
        return group;
    }


    public Set<PosixFilePermission> permissions() {
        int bits = (st_mode & UnixConstants.S_IAMB);
        HashSet<PosixFilePermission> perms = new HashSet<PosixFilePermission>();

        if ((bits & UnixConstants.S_IRUSR) > 0)
            perms.add(PosixFilePermission.OWNER_READ);
        if ((bits & UnixConstants.S_IWUSR) > 0)
            perms.add(PosixFilePermission.OWNER_WRITE);
        if ((bits & UnixConstants.S_IXUSR) > 0)
            perms.add(PosixFilePermission.OWNER_EXECUTE);

        if ((bits & UnixConstants.S_IRGRP) > 0)
            perms.add(PosixFilePermission.GROUP_READ);
        if ((bits & UnixConstants.S_IWGRP) > 0)
            perms.add(PosixFilePermission.GROUP_WRITE);
        if ((bits & UnixConstants.S_IXGRP) > 0)
            perms.add(PosixFilePermission.GROUP_EXECUTE);

        if ((bits & UnixConstants.S_IROTH) > 0)
            perms.add(PosixFilePermission.OTHERS_READ);
        if ((bits & UnixConstants.S_IWOTH) > 0)
            perms.add(PosixFilePermission.OTHERS_WRITE);
        if ((bits & UnixConstants.S_IXOTH) > 0)
            perms.add(PosixFilePermission.OTHERS_EXECUTE);

        return perms;
    }

    // wrap this object with BasicFileAttributes object to prevent leaking of
    // user information
    BasicFileAttributes asBasicFileAttributes() {
        return UnixAsBasicFileAttributes.wrap(this);
    }

    // unwrap BasicFileAttributes to get the underlying UnixFileAttributes
    // object. Returns null is not wrapped.
    static UnixFileAttributes toUnixFileAttributes(BasicFileAttributes attrs) {
        if (attrs instanceof UnixFileAttributes)
            return (UnixFileAttributes)attrs;
        if (attrs instanceof UnixAsBasicFileAttributes) {
            return ((UnixAsBasicFileAttributes)attrs).unwrap();
        }
        return null;
    }

    // wrap a UnixFileAttributes object as a BasicFileAttributes
    private static class UnixAsBasicFileAttributes implements BasicFileAttributes {
        private final UnixFileAttributes attrs;

        private UnixAsBasicFileAttributes(UnixFileAttributes attrs) {
            this.attrs = attrs;
        }

        static UnixAsBasicFileAttributes wrap(UnixFileAttributes attrs) {
            return new UnixAsBasicFileAttributes(attrs);
        }

        UnixFileAttributes unwrap() {
            return attrs;
        }


        public long lastModifiedTime() {
            return attrs.lastModifiedTime();
        }

        public long lastAccessTime() {
            return attrs.lastAccessTime();
        }

        public long creationTime() {
            return attrs.creationTime();
        }

        public TimeUnit resolution() {
            return attrs.resolution();
        }

        public boolean isRegularFile() {
            return attrs.isRegularFile();
        }

        public boolean isDirectory() {
            return attrs.isDirectory();
        }

        public boolean isSymbolicLink() {
            return attrs.isSymbolicLink();
        }

        public boolean isOther() {
            return attrs.isOther();
        }

        public long size() {
            return attrs.size();
        }

        public int linkCount() {
            return attrs.linkCount();
        }

        public Object fileKey() {
            return attrs.fileKey();
        }
    }
}
