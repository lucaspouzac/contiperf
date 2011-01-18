/*
 * (c) Copyright 2011 by Volker Bergmann. All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, is permitted under the terms of the
 * GNU Lesser General Public License (LGPL), Eclipse Public License (EPL) 
 * and the BSD License.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
 * WITHOUT A WARRANTY OF ANY KIND. ALL EXPRESS OR IMPLIED CONDITIONS,
 * REPRESENTATIONS AND WARRANTIES, INCLUDING ANY IMPLIED WARRANTY OF
 * MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE OR NON-INFRINGEMENT, ARE
 * HEREBY EXCLUDED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE
 * LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
 * CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF
 * SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS
 * INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN
 * CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
 * ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE
 * POSSIBILITY OF SUCH DAMAGE.
 */

package org.databene.stat;

import static org.junit.Assert.*;

import org.databene.stat.CounterRepository;
import org.databene.stat.LatencyCounter;
import org.junit.After;
import org.junit.Test;

/**
 * Tests the {@link CounterRepository}.<br/><br/>
 * Created: 14.01.2011 11:34:25
 * @since 1.08
 * @author Volker Bergmann
 */
public class CounterRepositoryTest {

	private static final String NAME = "CounterRepositoryTest";
	
	CounterRepository repository = CounterRepository.getInstance();

	@After
	public void tearDown() {
		repository.clear();
	}
	
	@Test
	public void testLifeCyle() {
		assertNull("Counter should not be defined yet", repository.getCounter(NAME));
		repository.addSample(NAME, 100);
		LatencyCounter counter = repository.getCounter(NAME);
		assertNotNull("Counter should have been defined after calling addSample()", counter);
		assertTrue("repository is expected to return the same counter instance on subsequent calls to getCounter()", 
				counter == repository.getCounter(NAME));
		repository.clear();
		assertNull("After calling clear(), the repository should have no counters", repository.getCounter(NAME));
	}
	
}
