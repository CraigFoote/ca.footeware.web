/**
 * 
 */
package ca.footeware.web.tests;

import javax.management.ServiceNotFoundException;

import org.junit.Assert;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import ca.footeware.web.services.NextSequenceService;

/**
 * @author Footeware.ca
 *
 */
@RunWith(SpringRunner.class)
@SpringBootTest
class NextSequenceServiceTest {

	@Autowired
	private NextSequenceService nss;

	/**
	 * Test method for
	 * {@link ca.footeware.web.services.NextSequenceService#getNextSequence(java.lang.String)}.
	 * 
	 * @throws ServiceNotFoundException if shit goes sideways
	 */
	@Test
	final void testGetNextSequence() throws ServiceNotFoundException {
		String nextSequence1 = nss.getNextSequence("customSequences");
		String nextSequence2 = nss.getNextSequence("customSequences");
		int ns1 = Integer.parseInt(nextSequence1);
		int ns2 = Integer.parseInt(nextSequence2);
		Assert.assertEquals("Numbers were not sequential.", ns2, ns1 + 1);
	}

}
