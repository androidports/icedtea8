/*
 * Copyright 2001-2003 Sun Microsystems, Inc.  All Rights Reserved.
 * Copyright 2009 Red Hat, Inc.
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

package sun.nio.ch;

import java.io.IOException;
import java.nio.channels.ClosedChannelException;
import java.nio.channels.FileChannel;

import org.classpath.icedtea.java.nio.channels.AsynchronousFileChannel;
import org.classpath.icedtea.java.nio.channels.FileLock;

public class AsynchronousFileLockImpl
    extends FileLock
{
    boolean valid;

    AsynchronousFileLockImpl(AsynchronousFileChannel channel, long position, long size, boolean shared)
    {
        super(channel, position, size, shared);
        this.valid = true;
    }

    public synchronized boolean isValid() {
        return valid;
    }

    synchronized void invalidate() {
        valid = false;
    }

    public synchronized void release() throws IOException {
        Channel ch = acquiredBy();
        if (!ch.isOpen())
            throw new ClosedChannelException();
        if (valid) {
	  if (ch instanceof AsynchronousFileChannelImpl)
	    ((AsynchronousFileChannelImpl)ch).release(this);
	  else throw new AssertionError("Attempted to release unsupported channel " + ch);
	  valid = false;
        }
    }

}
