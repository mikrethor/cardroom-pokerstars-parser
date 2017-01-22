package fr.mikrethor.cardroom.parser;

import java.io.File;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Scanner;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import fr.mikrethor.cardroom.enums.Currency;
import fr.mikrethor.cardroom.enums.GameType;
import fr.mikrethor.cardroom.pojo.Action;
import fr.mikrethor.cardroom.pojo.Hand;
import fr.mikrethor.cardroom.pojo.InfoSession;
import fr.mikrethor.cardroom.pojo.Player;

/**
 * Parsing Pokerstars.
 * 
 * @author Thor
 * 
 */
public class PokerstarsParser extends CardroomFileParser implements ICardroomParser {
	/**
	 * LOGGER.
	 */
	private static final Logger LOGGER = LoggerFactory.getLogger(PokerstarsParser.class);

	public static final String UTF8_BOM = "\uFEFF";
	public static final String HOLE_CARDS = "*** HOLE CARDS ***";
	public static final String FLOP = "*** FLOP ***";
	public static final String TURN = "*** TURN ***";
	public static final String RIVER = "*** RIVER ***";
	public static final String SHOW_DOWN = "*** SHOW DOWN ***";
	public static final String SUMMARY = "*** SUMMARY ***";
	public static final String NEW_HAND = "PokerStars Hand";
	public static final String SEAT = "Seat";
	public static final String BOARD = "Board";
	public static final String CASH_GAME = "CashGame";

	public static final String TABLE = "Table";
	public static final String ENCODING = "UTF8";

	@Override
	public String parseNewHandLine(String nextLine, Scanner input, String phase, String[] nextPhases,
			InfoSession infoSession, Hand hand) {
		// TODO Auto-generated method stub
		return null;
	}

	protected PokerstarsParser(File fileToParse) {
		super(fileToParse);
		if (LOGGER.isDebugEnabled() && fileToParse != null) {
			LOGGER.debug("{} : {}", this.getClass().getName(), fileToParse.getName());
		}
	}

	@Override
	public String parseTableLine(String nextLine, Scanner input, String phase, String[] nextPhases,
			InfoSession infoSession, Hand hand) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String parseSeatLine(String nextLine, Scanner input, String phase, String[] nextPhases, InfoSession game,
			Hand hand) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String parseAntesAndBlinds(String nextLine, Scanner input, String phase, String[] nextPhases,
			InfoSession game, Hand hand) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String parseDealer(String nextLine, Scanner input, String phase, String[] nextPhases, Hand hand) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String parseActionsByPhase(String nextLine, Scanner input, Hand hand, String phase, String[] nextPhases,
			List<Action> actions) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String parsePreflop(String nextLine, Scanner input, Hand hand) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String parseFlop(String nextLine, Scanner input, Hand hand) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String parseTurn(String nextLine, Scanner input, Hand hand) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String parseRiver(String nextLine, Scanner input, Hand hand) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String parseShowdown(String nextLine, Scanner input, Hand hand) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String parseSummary(String nextLine, Scanner input, InfoSession session, String phase, String[] nextPhases,
			Hand hand) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Action parseAction(String chaine, Map<String, Player> players) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getTournamentId() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public InfoSession parsing() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Double parseBuyIn(String chaine) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Double parseFee(String chaine) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public int parseLevel(String chaine) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public String parseHandIdSite(String chaine) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Double parseSmallBlind(String chaine) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Double parseBigBlind(String chaine) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String parseTableId(String chaine) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String parseNumberOfPlayerByTable(String chaine) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String parseGameIdSite(String chaine) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Integer parseButtonSeat(String chaine) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Player parsePlayerSeat(String chaine) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Double parseTotalPot(String chaine) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Double parseRake(String chaine) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String parsePlayerAccount(String chaine) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public GameType getGameTypeFromFilename(String fileName) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Date parseHandDate(String chaine) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Currency parseCurrency(String chaine) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Boolean isUselesLine(String line) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Map<String, StringBuffer> fileToMap() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Hand textToHandDto(StringBuffer text) {
		// TODO Auto-generated method stub
		return null;
	}

}
