package fr.mikrethor.cardroom.parser;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

import java.io.File;
import java.util.List;
import java.util.Map;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import fr.mikrethor.cardroom.enums.Card;
import fr.mikrethor.cardroom.enums.Currency;
import fr.mikrethor.cardroom.enums.Domain;
import fr.mikrethor.cardroom.enums.EAction;
import fr.mikrethor.cardroom.pojo.Action;
import fr.mikrethor.cardroom.pojo.Cardroom;
import fr.mikrethor.cardroom.pojo.Hand;
import fr.mikrethor.cardroom.pojo.Player;

@RunWith(JUnit4.class)
public class PokerstarsParserCashGameTest {
	/**
	 * LOGGER.
	 */
	private static final Logger LOGGER = LoggerFactory.getLogger(PokerstarsParserCashGameTest.class);

	/**
	 * Generic testmethod to parse PokerStars file.
	 * 
	 * @param fileName
	 *            name of the file to parse.
	 * @param nbPlayer
	 *            number of players in the game.
	 * @param nbHands
	 *            number of hands played during the game.
	 */
	private Map<String, Hand> genericTest(final String fileName, final int nbPlayer, final int nbHands) {
		LOGGER.info("Demarrage du parsing pokerstars avec le fichier : {}", fileName);
		final File file = new File(fileName);
		final PokerstarsParser siteParsing = new PokerstarsParserCashGame(file);
		siteParsing.setCurrency(Currency.USD);
		siteParsing.setCardroom(new Cardroom("Pokerstars", Domain.COM));
		Map<String, Hand> hands = null;

		assertEquals(true, file.exists());
		assertEquals(true, file.isFile());
		// Parsing
		final long debut = System.currentTimeMillis();
		hands = siteParsing.parse();
		final long fin = System.currentTimeMillis();
		assertNotNull(hands);

		LOGGER.info("Parsing time : " + (fin - debut) + " milliseconds");
		System.out.println("Parsing time : " + (fin - debut) + " milliseconds");

		assertEquals(nbHands, hands.size());
		assertEquals(nbPlayer, hands.get("145255388173").getNbPlayersOnOneTable());
		return hands;
	}

	@Test
	public void parsingCCFile() {
		final Map<String, Hand> hands = genericTest(
				"./target/test-classes/HH20151212 Tethys II - 0,01 $-0,02 $ - USD Hold'em No Limit.txt", 6, 109);

		final Hand main1 = hands.get("145255498017");

		final Player meanderMusic = main1.getPlayers().get(1);
		final Player jscialab = main1.getPlayers().get(2);
		final Player yusef24 = main1.getPlayers().get(3);
		final Player crazyChips = main1.getPlayers().get(4);
		final Player harmony21 = main1.getPlayers().get(5);
		final Player deennPL = main1.getPlayers().get(6);

		// Check players seats
		assertEquals(Integer.valueOf(1), meanderMusic.getSeat());
		assertEquals(Integer.valueOf(2), jscialab.getSeat());
		assertEquals(Integer.valueOf(3), yusef24.getSeat());
		assertEquals(Integer.valueOf(4), crazyChips.getSeat());
		assertEquals(Integer.valueOf(5), harmony21.getSeat());
		assertEquals(Integer.valueOf(6), deennPL.getSeat());
		// Check players names
		assertEquals("MeanderMusic", meanderMusic.getName());
		assertEquals("jscialab", jscialab.getName());
		assertEquals("yusef24", yusef24.getName());
		assertEquals("-Cr@zyChips-", crazyChips.getName());
		assertEquals("harmony21", harmony21.getName());
		assertEquals("DeennPL", deennPL.getName());

		// Check last player stack
		assertEquals(new Double(0.8), new Double(meanderMusic.getStack()));
		assertEquals(new Double(3.87), new Double(jscialab.getStack()));
		assertEquals(new Double(0.65), new Double(yusef24.getStack()));
		assertEquals(new Double(2), new Double(crazyChips.getStack()));
		assertEquals(new Double(2.52), new Double(harmony21.getStack()));
		assertEquals(new Double(1), new Double(deennPL.getStack()));

		assertEquals(Double.valueOf(0.02), Double.valueOf(main1.getBigBlind()));
		assertEquals(Double.valueOf(0.01), Double.valueOf(main1.getSmallBlind()));

		assertEquals(deennPL, main1.getBigBlindPlayer());
		assertEquals(jscialab, main1.getSmallBlindPlayer());
		assertEquals(meanderMusic, main1.getDealerPlayer());
		assertEquals(crazyChips, main1.getPlayer());

		assertEquals("Tethys II", main1.getIdTable());

		assertEquals(Double.valueOf(0.02), Double.valueOf(main1.getRake()));
		assertEquals(Double.valueOf(0.46), Double.valueOf(main1.getTotalPot()));
		// [5h 4d Jh]
		final Card[] flop = main1.getFlop();
		assertEquals(Card.C_5H, flop[0]);
		assertEquals(Card.C_4D, flop[1]);
		assertEquals(Card.C_JH, flop[2]);

		// [8c]
		final Card turn = main1.getTurn();
		assertEquals(Card.C_8C, turn);

		// [8d]
		final Card river = main1.getRiver();
		assertEquals(Card.C_8D, river);

		assertEquals(Integer.valueOf(6), Integer.valueOf(main1.getPreflopActions().size()));

		final List<Action> listPreFlopActions = main1.getPreflopActions();

		assertEquals(crazyChips, listPreFlopActions.get(0).getPlayer());
		assertEquals(EAction.FOLDS, listPreFlopActions.get(0).getAction());
		assertEquals(harmony21, listPreFlopActions.get(1).getPlayer());
		assertEquals(EAction.FOLDS, listPreFlopActions.get(1).getAction());
		assertEquals(deennPL, listPreFlopActions.get(2).getPlayer());
		assertEquals(EAction.CHECKS, listPreFlopActions.get(2).getAction());
		assertEquals(meanderMusic, listPreFlopActions.get(3).getPlayer());
		assertEquals(EAction.CALLS, listPreFlopActions.get(3).getAction());
		assertEquals(jscialab, listPreFlopActions.get(4).getPlayer());
		assertEquals(EAction.FOLDS, listPreFlopActions.get(4).getAction());
		assertEquals(yusef24, listPreFlopActions.get(5).getPlayer());
		assertEquals(EAction.CHECKS, listPreFlopActions.get(5).getAction());

		assertEquals(Integer.valueOf(3), Integer.valueOf(main1.getFlopActions().size()));

		final List<Action> listFlopActions = main1.getFlopActions();

		assertEquals(yusef24, listFlopActions.get(0).getPlayer());
		assertEquals(EAction.CHECKS, listFlopActions.get(0).getAction());
		assertEquals(deennPL, listFlopActions.get(1).getPlayer());
		assertEquals(EAction.CHECKS, listFlopActions.get(1).getAction());
		assertEquals(meanderMusic, listFlopActions.get(2).getPlayer());
		assertEquals(EAction.CHECKS, listFlopActions.get(2).getAction());

		final List<Action> listTurnActions = main1.getTurnActions();
		assertEquals(Integer.valueOf(3), Integer.valueOf(listTurnActions.size()));

		assertEquals(yusef24, listTurnActions.get(0).getPlayer());
		assertEquals(EAction.BETS, listTurnActions.get(0).getAction());
		assertEquals(Double.valueOf(0.03), Double.valueOf(listTurnActions.get(0).getMontant()));
		assertEquals(deennPL, listTurnActions.get(1).getPlayer());
		assertEquals(EAction.CALLS, listTurnActions.get(1).getAction());
		assertEquals(Double.valueOf(0.03), Double.valueOf(listTurnActions.get(1).getMontant()));
		assertEquals(meanderMusic, listTurnActions.get(2).getPlayer());
		assertEquals(EAction.CALLS, listTurnActions.get(2).getAction());
		assertEquals(Double.valueOf(0.03), Double.valueOf(listTurnActions.get(2).getMontant()));

		final List<Action> listRiverActions = main1.getRiverActions();
		assertEquals(Integer.valueOf(3), Integer.valueOf(listRiverActions.size()));

		assertEquals(yusef24, listRiverActions.get(0).getPlayer());
		assertEquals(EAction.BETS, listRiverActions.get(0).getAction());
		assertEquals(Double.valueOf(0.15), Double.valueOf(listRiverActions.get(0).getMontant()));
		assertEquals(deennPL, listRiverActions.get(1).getPlayer());
		assertEquals(EAction.CALLS, listRiverActions.get(1).getAction());
		assertEquals(Double.valueOf(0.15), Double.valueOf(listRiverActions.get(1).getMontant()));
		assertEquals(meanderMusic, listRiverActions.get(2).getPlayer());
		assertEquals(EAction.FOLDS, listRiverActions.get(2).getAction());
		assertEquals(Double.valueOf(0), Double.valueOf(listRiverActions.get(2).getMontant()));

		final List<Action> listShowdownActions = main1.getShowdownActions();
		assertEquals(Integer.valueOf(3), Integer.valueOf(listShowdownActions.size()));

		assertEquals(yusef24, listShowdownActions.get(0).getPlayer());
		assertEquals(EAction.SHOWS, listShowdownActions.get(0).getAction());
		assertEquals(Card.C_3H, listShowdownActions.get(0).getCards()[0]);
		assertEquals(Card.C_9D, listShowdownActions.get(0).getCards()[1]);
		assertEquals(deennPL, listShowdownActions.get(1).getPlayer());
		assertEquals(EAction.SHOWS, listShowdownActions.get(1).getAction());
		assertEquals(Card.C_8S, listShowdownActions.get(1).getCards()[0]);
		assertEquals(Card.C_3C, listShowdownActions.get(1).getCards()[1]);
		assertEquals(deennPL, listShowdownActions.get(2).getPlayer());
		assertEquals(EAction.COLLECTED, listShowdownActions.get(2).getAction());
		assertNull(listShowdownActions.get(2).getCards());

		// [2s Qh]
		final Card[] crazyChipsCards = main1.getMapPlayerCards().get("-Cr@zyChips-");
		assertEquals(Card.C_2S, crazyChipsCards[0]);
		assertEquals(Card.C_QH, crazyChipsCards[1]);
		// [3h 9d]
		final Card[] yusef24Cards = main1.getMapPlayerCards().get("yusef24");
		assertEquals(Card.C_3H, yusef24Cards[0]);
		assertEquals(Card.C_9D, yusef24Cards[1]);
		// [8s 3c]
		final Card[] deennPLards = main1.getMapPlayerCards().get("DeennPL");
		assertEquals(Card.C_8S, deennPLards[0]);
		assertEquals(Card.C_3C, deennPLards[1]);

	}

}