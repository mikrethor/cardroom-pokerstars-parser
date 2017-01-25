package fr.mikrethor.cardroom.parser;

import java.io.File;
import java.text.ParseException;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Scanner;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import fr.mikrethor.cardroom.enums.Card;
import fr.mikrethor.cardroom.enums.Currency;
import fr.mikrethor.cardroom.enums.GameType;
import fr.mikrethor.cardroom.pojo.Action;
import fr.mikrethor.cardroom.pojo.Hand;
import fr.mikrethor.cardroom.pojo.InfoSession;
import fr.mikrethor.cardroom.pojo.Player;
import fr.mikrethor.cardroom.utils.DateUtils;
import fr.mikrethor.cardroom.utils.RomanNumeralUtils;

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
		final String nextL = nextLine;
		if (nextL.startsWith(phase)) {
			if (LOGGER.isDebugEnabled()) {
				LOGGER.debug(NEW_HAND);
			}
			final String[] tab = nextL.split(SPACE);
			GameType gameType = GameType.CASH;
			if (GameType.TOURNAMENT.getType().equals(tab[3])) {
				gameType = GameType.TOURNAMENT;
			}

			if (GameType.TOURNAMENT.equals(gameType)) {
				hand.setLevel(parseLevel(nextL));
				final Date handDate = parseHandDate(nextL);
				hand.setDate(handDate.getTime());
			}
			hand.setLabelGame(getTournamentId());
			hand.setLabel(parseHandIdSite(nextL));
			hand.setId(hand.getLabel());
			final Date handDate = parseHandDate(nextL);
			hand.setDate(handDate.getTime());
			hand.setBigBlind(parseBigBlind(nextL));
			hand.setSmallBlind(parseSmallBlind(nextL));
			hand.setCurrency(parseCurrency(nextL));

		}
		return nextL;
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
		final String[] tab = chaine.split(SPACE);
		int level = 0;
		// Case heads-up with rebuy
		if ("Round".equals(tab[12])) {
			level = RomanNumeralUtils.toInt(tab[15]);
		} else {
			level = RomanNumeralUtils.toInt(tab[12]);
		}
		if (LOGGER.isDebugEnabled()) {
			LOGGER.debug("level {} from roman {}", level, tab[12]);
		}
		return level;
	}

	@Override
	public String parseHandIdSite(String chaine) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Double parseSmallBlind(String chaine) {
		final int startPosition = chaine.indexOf(LEFT_PARENTHESIS) + 1;
		final int endPosition = chaine.indexOf(SLASH);
		final String smallBlind = chaine.substring(startPosition, endPosition);
		return Double.parseDouble(smallBlind);
	}

	@Override
	public Double parseBigBlind(String chaine) {
		final int startPosition = chaine.indexOf(SLASH) + 1;
		final int endPosition = chaine.indexOf(RIGHT_PARENTHESIS);
		final String bigBlind = chaine.substring(startPosition, endPosition);
		return Double.parseDouble(bigBlind);
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
		final String[] tabTotalPot = chaine.split(SPACE);
		String totalPot = tabTotalPot[2].replace(money.getSymbol(), EMPTY);
		totalPot = totalPot.replace(money.getSymbol(), EMPTY);

		return Double.parseDouble(totalPot);
	}

	@Override
	public Double parseRake(String chaine) {
		final int startPosition = chaine.indexOf("| Rake") + "| Rake".length() + 1;
		final int endPosition = chaine.length();
		String rake = chaine.substring(startPosition, endPosition);
		rake = rake.replace(money.getSymbol(), EMPTY);
		if (LOGGER.isDebugEnabled()) {
			LOGGER.debug("rake {}", rake);
		}
		return Double.parseDouble(rake);
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
		final int startPosition = chaine.lastIndexOf(DASH) + 1;
		int endPosition = chaine.lastIndexOf(SPACE);
		if (chaine.lastIndexOf(CLOSING_SQUARE_BRACKET) > 0) {
			endPosition = chaine.lastIndexOf(OPENNING_SQUARE_BRACKET);
		}

		chaine = chaine.substring(startPosition, endPosition);
		try {
			return DateUtils.toDate(chaine, "yyyy/MM/dd HH:mm:ss z");
		} catch (final ParseException e) {
			LOGGER.error(e.getMessage(), e);
			return new Date();
		}
	}

	@Override
	public Currency parseCurrency(String chaine) {
		Currency result = null;
		for (Currency currency : Currency.values()) {
			if (chaine.indexOf(currency.getSymbol()) > 0) {
				result = currency;
			}
		}
		// TODO si currency null error ?;
		return result;
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

	public Player parsePlayerSummary(String chaine) {
		final int espace = chaine.indexOf(SPACE);
		final int deuxpoints = chaine.indexOf(COLON);

		final String seat = chaine.substring(espace + 1, deuxpoints);
		final String player = chaine.substring(deuxpoints + 2, chaine.indexOf(SPACE, deuxpoints + 2));
		Card[] cards = null;
		final Player playerDTO = new Player(cardRoom, player);
		if (chaine.indexOf(OPENNING_SQUARE_BRACKET) > 0) {
			cards = parseCards(chaine);
			playerDTO.setCards(cards);
		}
		playerDTO.setSeat(Integer.parseInt(seat));

		if (LOGGER.isDebugEnabled()) {
			LOGGER.debug("seat : " + seat + ", player : " + player + ", cards : " + cards);
		}

		return playerDTO;
	}

}
