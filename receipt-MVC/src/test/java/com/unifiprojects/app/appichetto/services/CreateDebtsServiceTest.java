package com.unifiprojects.app.appichetto.services;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.math3.util.Pair;
import org.junit.Before;
import org.junit.Test;

import com.unifiprojects.app.appichetto.models.Accounting;
import com.unifiprojects.app.appichetto.models.Item;
import com.unifiprojects.app.appichetto.models.Receipt;
import com.unifiprojects.app.appichetto.models.User;

public class CreateDebtsServiceTest {

	private CreateDebtsService createDebtsService;

	@Before
	public void setUp() {
		createDebtsService = new CreateDebtsService();
	}

	@Test
	public void testcalculateUserAccountingMapByItemPriceReturnAMapWithRightAccounting() {
		User buyer = new User("pippo", "");
		User participant = new User("pluto", "");
		Receipt receipt = new Receipt(buyer);
		Item item = new Item("Sugo", 2.4, 2, Arrays.asList(buyer, participant));
		Item item2 = new Item("Pasta", 2.4, 2, Arrays.asList(buyer, participant));
		receipt.addItem(item);
		receipt.addItem(item2);

		Map<User, Accounting> accountings = createDebtsService.calculateAccountingMapFromItemPrice(receipt);

		assertThat(accountings.values()).containsExactlyInAnyOrder(new Accounting(participant, 4.8));
	}

	@Test
	public void testCreateUserAccounitngMapReturnTheRightMap() {
		User buyer = new User("pippo", "");
		User participant1 = new User("pluto", "");
		User participant2 = new User("mario", "");
		Receipt receipt = new Receipt(buyer);
		Accounting accounting1 = new Accounting(participant1, 2.2);
		Accounting accounting2 = new Accounting(participant2, 1.3);
		receipt.setAccountingList(new ArrayList<>(Arrays.asList(accounting1, accounting2)));

		Map<User, Accounting> accountings = createDebtsService.createAccountingMapFromAccountingList(receipt.getAccountings());

		assertThat(accountings.get(participant1)).isEqualTo(accounting1);
		assertThat(accountings.get(participant2)).isEqualTo(accounting2);
	}

	@Test
	public void testCalculateUserAccountingMapWhenOneIsNoLongerOwner() {
		User participant1 = new User("pluto", "");
		User participant2 = new User("mario", "");
		Accounting oldAP1 = new Accounting(participant1, 2.2);
		Accounting oldAP2 = new Accounting(participant2, 1.3);
		Map<User, Accounting> oldAIPM = new HashMap<>();
		oldAIPM.put(participant1, oldAP1);
		oldAIPM.put(participant2, oldAP2);
		Map<User, Accounting> oldAM = new HashMap<>();
		oldAM.put(participant1, oldAP1);
		oldAM.put(participant2, oldAP2);
		Map<User, Accounting> aIPM = new HashMap<>();
		aIPM.put(participant1, oldAP1);

		Map<User, Accounting> accountingsMap = createDebtsService.calculateAccountingMap(oldAIPM, oldAM, aIPM);

		assertThat(accountingsMap.values()).containsExactlyInAnyOrder(oldAP1);
	}

	@Test
	public void testCalculateUserAccountingMapWhenAnAccountingIsPartiallyPaid() {
		User participant1 = new User("pluto", "");
		User participant2 = new User("mario", "");
		Accounting oldAIP1 = new Accounting(participant1, 2.2);
		Accounting oldA1 = new Accounting(participant1, 1.1);
		Accounting oldAIP2 = new Accounting(participant2, 1.3);
		Map<User, Accounting> oldAIPM = new HashMap<>();
		oldAIPM.put(participant1, oldAIP1);
		oldAIPM.put(participant2, oldAIP2);
		Map<User, Accounting> oldAM = new HashMap<>();
		oldAM.put(participant1, oldA1);
		oldAM.put(participant2, oldAIP2);
		Map<User, Accounting> aIPM = new HashMap<>();
		aIPM.put(participant1, oldAIP1);

		Map<User, Accounting> accountingsMap = createDebtsService.calculateAccountingMap(oldAIPM, oldAM, aIPM);

		assertThat(accountingsMap.values()).containsExactlyInAnyOrder(oldA1);
	}

	@Test
	public void testCalculateUserAccountingMapWhenAccountingIsPartiallyPaidAndThenTheOwnerNotParticipate() {
		User participant1 = new User("pluto", "");
		User participant2 = new User("mario", "");
		Accounting oldAIP1 = new Accounting(participant1, 2.2);
		Accounting oldA1 = new Accounting(participant1, 1.1);
		Accounting oldAIP2 = new Accounting(participant2, 1.3);
		Map<User, Accounting> oldAIPM = new HashMap<>();
		oldAIPM.put(participant1, oldAIP1);
		oldAIPM.put(participant2, oldAIP2);
		Map<User, Accounting> oldAM = new HashMap<>();
		oldAM.put(participant1, oldA1);
		oldAM.put(participant2, oldAIP2);
		Map<User, Accounting> aIPM = new HashMap<>();
		aIPM.put(participant2, oldAIP2);

		Map<User, Accounting> accountingsMap = createDebtsService.calculateAccountingMap(oldAIPM, oldAM, aIPM);

		assertThat(accountingsMap.values()).containsExactlyInAnyOrder(oldAIP2, new Accounting(participant1, -1.1));
	}

	@Test
	public void testCreateRefundReceiptSetAccountingToZeroAndCreateCorrectlyTheReceipt() {
		User buyer = new User("pippo", "");
		User participant1 = new User("pluto", "");
		User participant2 = new User("mario", "");
		Accounting accountingParticipant1 = new Accounting(participant1, -2.2);
		Accounting accountingParticipant2 = new Accounting(participant2, 2.2);
		Map<User, Accounting> userAccountingMap = new HashMap<>();
		userAccountingMap.put(participant1, accountingParticipant1);
		userAccountingMap.put(participant2, accountingParticipant2);
		Receipt refundReceipt = new Receipt(participant1);
		refundReceipt.addAccounting(new Accounting(buyer, 2.2));
		refundReceipt.setDescription("Refund receipt");

		List<Receipt> receipts = createDebtsService.createRefundReceipts(userAccountingMap, buyer);
		assertThat(receipts).containsExactlyInAnyOrder(refundReceipt);
	}

	@Test
	public void testComputeDebdtsCreateRefundReceiptAndAccountingCorrectly() {
		User buyer = new User("pippo", "");
		User participant1 = new User("pluto", "");
		User participant2 = new User("mario", "");

		Item item1 = new Item("Sugo", 1.1, 2, Arrays.asList(participant1));

		Accounting oldAIPP1 = new Accounting(participant1, 2.2);
		Accounting oldAIPP2 = new Accounting(participant2, 2.2);
		Accounting oldAP1 = new Accounting(participant1, 1.1);
		Accounting oldAP2 = new Accounting(participant2, 2.0);

		Receipt receipt = new Receipt(buyer);
		receipt.addItem(item1);
		receipt.addAccounting(oldAP1);
		receipt.addAccounting(oldAP2);

		Map<User, Accounting> oldAccountingsByItemPriceMap = new HashMap<>();
		oldAccountingsByItemPriceMap.put(participant1, oldAIPP1);
		oldAccountingsByItemPriceMap.put(participant2, oldAIPP2);

		Pair<List<Accounting>, List<Receipt>> totalDebts = createDebtsService
				.computeAccountingDebtsAndRefoundReceipts(receipt, oldAccountingsByItemPriceMap);

		List<Accounting> accountings = totalDebts.getFirst();
		List<Receipt> refundReceipts = totalDebts.getSecond();

		Receipt aspectedRefundReceipt = new Receipt(participant2);
		aspectedRefundReceipt.addAccounting(new Accounting(buyer, 0.2));
		aspectedRefundReceipt.setDescription("Refund receipt");

		assertThat(accountings).containsExactlyInAnyOrder(new Accounting(participant1, 1.1));
		assertThat(refundReceipts).containsExactlyInAnyOrder(aspectedRefundReceipt);
	}
}
