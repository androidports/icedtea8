/* IcedTeaPluginRequestProcessor.h

   Copyright (C) 2009  Red Hat

This file is part of IcedTea.

IcedTea is free software; you can redistribute it and/or modify
it under the terms of the GNU General Public License as published by
the Free Software Foundation; either version 2, or (at your option)
any later version.

IcedTea is distributed in the hope that it will be useful, but
WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
General Public License for more details.

You should have received a copy of the GNU General Public License
along with IcedTea; see the file COPYING.  If not, write to the
Free Software Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA
02110-1301 USA.

Linking this library statically or dynamically with other modules is
making a combined work based on this library.  Thus, the terms and
conditions of the GNU General Public License cover the whole
combination.

As a special exception, the copyright holders of this library give you
permission to link this library with independent modules to produce an
executable, regardless of the license terms of these independent
modules, and to copy and distribute the resulting executable under
terms of your choice, provided that you also meet, for each linked
independent module, the terms and conditions of the license of that
module.  An independent module is a module which is not derived from
or based on this library.  If you modify this library, you may extend
this exception to your version of the library, but you are not
obligated to do so.  If you do not wish to do so, delete this
exception statement from your version. */

#ifndef __ICEDTEAPLUGINREQUESTPROCESSOR_H__
#define __ICEDTEAPLUGINREQUESTPROCESSOR_H__

#include <map>
#include <stdio.h>
#include <stdlib.h>
#include <pthread.h>
#include <time.h>

#include <npapi.h>

#if MOZILLA_VERSION_COLLAPSED < 1090100
#include <npupp.h>
#else
#include <npapi.h>
#include <npruntime.h>
#endif

#include "IcedTeaPluginUtils.h"
#include "IcedTeaJavaRequestProcessor.h"

/**
 * Data structure passed to functions called in a new thread.
 */

typedef struct async_call_thread_data
{
    std::vector<void*> parameters;
	std::string result;
	bool result_ready;
	bool call_successful;
} AsyncCallThreadData;

/* Internal request reference counter */
static long internal_req_ref_counter;

/* Given a value and type, performs the appropriate Java->JS type
 * mapping and puts it in the given variant */

static void convertToNPVariant(std::string value, std::string type, NPVariant* result_variant);

// Internal methods that need to run in main thread
void _getMember(void* data);
void _setMember(void* data);
void _call(void* data);
void _eval(void* data);
void _getString(void* data);

static pthread_mutex_t tc_mutex = PTHREAD_MUTEX_INITIALIZER;
static int thread_count = 0;

void* queue_processor(void* data);

/* Mutex to ensure that the request queue is accessed synchronously */
extern pthread_mutex_t message_queue_mutex;

/* Mutex to ensure synchronized writes */
extern pthread_mutex_t syn_write_mutex;

/* Queue for holding messages that get processed in a separate thread */
extern std::vector< std::vector<std::string>* >* message_queue;

/**
 * Processes requests made TO the plugin (by java or anyone else)
 */
class PluginRequestProcessor : public BusSubscriber
{
    private:

    	/* Requests that are still pending */
    	std::map<pthread_t, uintmax_t>* pendingRequests;

    	/* Dispatch request processing to a new thread for asynch. processing */
    	void dispatch(void* func_ptr (void*), std::vector<std::string>* message, std::string* src);

    	/* Send main window pointer to Java */
    	void sendWindow(std::vector<std::string>* message_parts);

    	/* Stores the variant on java side */
    	void storeVariantInJava(NPVariant variant, std::string* result);

    public:
    	PluginRequestProcessor(); /* Constructor */
    	~PluginRequestProcessor(); /* Destructor */

    	/* Process new requests (if applicable) */
        virtual bool newMessageOnBus(const char* message);

        /* Send member ID to Java */
        void sendMember(std::vector<std::string>* message_parts);

        /* Set member to given value */
        void setMember(std::vector<std::string>* message_parts);

        /* Send string value of requested object */
        void sendString(std::vector<std::string>* message_parts);

        /* Evaluate the given script */
        void eval(std::vector<std::string>* message_parts);

        /* Evaluate the given script */
        void call(std::vector<std::string>* message_parts);

        /* Decrements reference count for given object */
        void finalize(std::vector<std::string>* message_parts);
};

#endif // __ICEDTEAPLUGINREQUESTPROCESSOR_H__
