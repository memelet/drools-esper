package memelet.droolsesper;

import org.drools.WorkingMemory;
import org.drools.event.rule.AfterActivationFiredEvent;
import org.drools.event.rule.AgendaEventListener
import org.drools.event.rule.DefaultAgendaEventListener
import org.drools.event.rule.DebugWorkingMemoryEventListener
import org.drools.event.rule.DebugAgendaEventListener

import java.util.concurrent.TimeUnit;
import static java.util.concurrent.TimeUnit.*;

import org.junit.Rule
import org.junit.Test
import org.junit.Before
import org.junit.Ignore

public class Pattern03Test extends AbstractEsperEventPatternsTest {

	def List<String> drlFilenames() { ["Pattern_03.drl"] }

	// TODO Figure out why this test fails but the one using the named entry
	// point does not
	@Ignore
	@Test 
	def void correlateEventsArrivingIn2OrMoreStreams() {
		// Not the same account
		insert WithdrawalEvent(id: "w1", accountNumber: "AAA", amount: 100)
		insert FraudWarningEvent(id: "f1", accountNumber: "BBB")
		fireAllRules
		assert results.isEmpty()

		// Not within the 30s window
		advanceTime 31.s
		advanceTime 1000.s
		insert FraudWarningEvent(id: "f2", accountNumber: "AAA")
		fireAllRules
		assert results.isEmpty()

		// Correlated
		advanceTime 60.s
		insert WithdrawalEvent(id: "w", accountNumber: "AAA", amount: 200)
		advanceTime 10.s
		insert FraudWarningEvent(id: "f", accountNumber: "AAA")
		fireAllRules
		
		//TODO assert results["fireCount"] == 1
		assert results["accountNumber"] == "AAA"
		assert results["amount"] == 200
	}
	
	@Test
	def void correlateEventsArrivingIn2OrMoreStreamsWithNamedEntryPoint() {
		// Not the same account
		entryPoint.insert WithdrawalEvent(id: "w1", accountNumber: "AAA", amount: 100)
		entryPoint.insert FraudWarningEvent(id: "f1", accountNumber: "BBB")
		fireAllRules
		assert results.isEmpty()

		// Not within the 30s window
		advanceTime 31.s
		advanceTime 1000.s
		entryPoint.insert FraudWarningEvent(id: "f2", accountNumber: "AAA")
		fireAllRules
		assert results.isEmpty()

		// Correlated
		advanceTime 60.s
		entryPoint.insert WithdrawalEvent(id: "w", accountNumber: "AAA", amount: 200)
		advanceTime 10.s
		entryPoint.insert FraudWarningEvent(id: "f", accountNumber: "AAA")
		fireAllRules
		
		//TODO assert results["fireCount"] == 1
		assert results["ep.accountNumber"] == "AAA"
		assert results["ep.amount"] == 200
	}
}
