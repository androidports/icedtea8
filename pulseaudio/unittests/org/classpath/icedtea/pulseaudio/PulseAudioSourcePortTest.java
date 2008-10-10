/* PulseAudioSourcePortTest.java
   Copyright (C) 2008 Red Hat, Inc.

This file is part of IcedTea.

IcedTea is free software; you can redistribute it and/or
modify it under the terms of the GNU General Public License as published by
the Free Software Foundation, version 2.

IcedTea is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
General Public License for more details.

You should have received a copy of the GNU General Public License
along with IcedTea; see the file COPYING.  If not, write to
the Free Software Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA
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
exception statement from your version.
 */

package org.classpath.icedtea.pulseaudio;

import static org.junit.Assert.assertNotNull;

import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.BooleanControl;
import javax.sound.sampled.FloatControl;
import javax.sound.sampled.Line;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.Mixer;
import javax.sound.sampled.Port;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class PulseAudioSourcePortTest {

	Mixer mixer;

	@Before
	public void setUp() throws Exception {
		Mixer.Info mixerInfos[] = AudioSystem.getMixerInfo();
		Mixer.Info selectedMixerInfo = null;
		// int i = 0;
		for (Mixer.Info info : mixerInfos) {
			// System.out.println("Mixer Line " + i++ + ": " + info.getName() +
			// " " + info.getDescription());
			if (info.getName().contains("PulseAudio")) {
				selectedMixerInfo = info;
			}
		}
		assertNotNull(selectedMixerInfo);
		mixer = AudioSystem.getMixer(selectedMixerInfo);
		assertNotNull(mixer);
		if (mixer.isOpen()) {
			mixer.close();
		}

		mixer.open();

	}

	@Test
	public void testClose() throws LineUnavailableException {
		Line.Info[] lineInfos = mixer.getSourceLineInfo();
		for (Line.Info info : lineInfos) {
			if (info.getLineClass() == Port.class) {
				System.out.println(info.toString());
				Port port = (Port) mixer.getLine(info);
				port.close();
			}
		}
	}

	@Test
	public void testControls() throws LineUnavailableException {
		Line.Info[] lineInfos = mixer.getSourceLineInfo();
		for (Line.Info info : lineInfos) {
			if (info.getLineClass() == Port.class) {
				System.out.println(info.toString());
				Port port = (Port) mixer.getLine(info);
				if (!port.isOpen()) {
					port.open();
				}
				FloatControl volumeControl = (FloatControl) port
						.getControl(FloatControl.Type.VOLUME);
				volumeControl.setValue(60000);
				BooleanControl muteControl = (BooleanControl) port
						.getControl(BooleanControl.Type.MUTE);
				muteControl.setValue(true);
				muteControl.setValue(false);
				port.close();
			}
		}
	}

	@After
	public void tearDown() {
		if (mixer.isOpen()) {
			mixer.close();
		}
	}

}
