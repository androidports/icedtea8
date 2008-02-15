/*
 * Copyright 2003-2007 Sun Microsystems, Inc.  All Rights Reserved.
 * Copyright 2007, 2008 Red Hat, Inc.
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS FILE HEADER.
 *
 * This code is free software; you can redistribute it and/or modify it
 * under the terms of the GNU General Public License version 2 only, as
 * published by the Free Software Foundation.
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
 *
 */

#include <entryFrame_zero.hpp>
#include <interpreterFrame_zero.hpp>

// Constructors

inline frame::frame()
{
  _sp = NULL;
  _pc = NULL;
  _cb = NULL;
  _deopt_state = unknown;
}

inline frame::frame(intptr_t* sp)
{
  _sp = sp;
  _pc = (address) sp;
  _cb = NULL;
  _deopt_state = not_deoptimized;
}

// Accessors

inline intptr_t* frame::sender_sp() const
{
  return (intptr_t *) ((JavaFrame *) sp())->next();
}

inline intptr_t* frame::link() const
{
  Unimplemented();
}

#ifdef CC_INTERP
inline interpreterState frame::get_interpreterState() const
{
  return ((InterpreterFrame *) sp())->interpreter_state();
}

inline intptr_t** frame::interpreter_frame_locals_addr() const
{
  assert(is_interpreted_frame(), "must be interpreted");
  return &(get_interpreterState()->_locals);
}

inline intptr_t* frame::interpreter_frame_bcx_addr() const
{
  assert(is_interpreted_frame(), "must be interpreted");
  return (intptr_t*) &(get_interpreterState()->_bcp);
}

inline constantPoolCacheOop* frame::interpreter_frame_cache_addr() const
{
  assert(is_interpreted_frame(), "must be interpreted");
  return &(get_interpreterState()->_constants);
}

inline methodOop* frame::interpreter_frame_method_addr() const
{
  assert(is_interpreted_frame(), "must be interpreted");
  return &(get_interpreterState()->_method);
}

inline intptr_t* frame::interpreter_frame_mdx_addr() const
{
  assert(is_interpreted_frame(), "must be interpreted");
  return (intptr_t*) &(get_interpreterState()->_mdx);
}

inline intptr_t* frame::interpreter_frame_tos_address() const
{
  assert(is_interpreted_frame(), "must be interpreted");
  return get_interpreterState()->_stack + 1;
}
#endif // CC_INTERP

inline int frame::interpreter_frame_monitor_size()
{
  return BasicObjectLock::size();
}

inline intptr_t* frame::interpreter_frame_expression_stack() const
{
  intptr_t* monitor_end = (intptr_t*) interpreter_frame_monitor_end();
  return monitor_end - 1;
}

inline jint frame::interpreter_frame_expression_stack_direction()
{
  return -1;
}

// Return a unique id for this frame. The id must have a value where
// we can distinguish identity and younger/older relationship. NULL
// represents an invalid (incomparable) frame.
inline intptr_t* frame::id() const
{
  return sp();
}

inline JavaCallWrapper* frame::entry_frame_call_wrapper() const
{
  return ((EntryFrame *) sp())->call_wrapper();
}

inline void frame::set_saved_oop_result(RegisterMap* map, oop obj)
{
  Unimplemented();
}

inline oop frame::saved_oop_result(RegisterMap* map) const
{
  Unimplemented();
}

inline bool frame::is_older(intptr_t* id) const
{
  Unimplemented();
}

inline intptr_t* frame::entry_frame_argument_at(int offset) const
{
  Unimplemented();
}

inline intptr_t* frame::unextended_sp() const
{
  Unimplemented();
}
