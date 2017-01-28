package fr.mikrethor.cardroom.parser;

import java.io.IOException;
import java.nio.file.Path;
import java.text.ParseException;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import fr.mikrethor.cardroom.enums.Card;
import fr.mikrethor.cardroom.enums.Currency;
import fr.mikrethor.cardroom.enums.EAction;
import fr.mikrethor.cardroom.enums.GameType;
import fr.mikrethor.cardroom.enums.Round;
import fr.mikrethor.cardroom.parser.pokerstars.Actions;
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

	protected PokerstarsParser(Path fileToParse) {
		super(fileToParse);
		if (LOGGER.isDebugEnabled() && fileToParse != null) {
			LOGGER.debug("{} : {}", this.getClass().getName(), fileToParse.getFileName());
		}
	}

	@Override
	public String parseTableLine(String nextLine, Scanner input, String phase, String[] nextPhases,
			InfoSession infoSession, Hand hand) {
		final String nextL = nextLine;

		if (nextL.startsWith(TABLE)) {
			if (LOGGER.isDebugEnabled()) {
				LOGGER.debug(TABLE);
			}
			final String numeroTable = parseTableId(nextL);

			final String nombreJoueur = parseNumberOfPlayerByTable(nextL);
			final Integer buttonSeat = parseButtonSeat(nextL);
			hand.setButtonSeat(buttonSeat);

			if (LOGGER.isDebugEnabled()) {
				LOGGER.debug("button seat {} in hand {}", buttonSeat, hand.getLabel());
				LOGGER.debug("number players {}, table number {}", nombreJoueur, numeroTable);
			}

			hand.setNbPlayersOnOneTable(Integer.parseInt(nombreJoueur));
			hand.setIdTable(numeroTable);

		}
		return nextL;
	}

	@Override
	public String parseSeatLine(String nextLine, Scanner input, String phase, String[] nextPhases, InfoSession game,
			Hand hand) {
		String nextL = nextLine;
		if (nextL.startsWith(SEAT)) {
			if (LOGGER.isDebugEnabled()) {
				LOGGER.debug(SEAT);
			}
			while (input.hasNext()) {
				if (nextL.startsWith(SEAT)) {
					if (LOGGER.isDebugEnabled()) {
						LOGGER.debug(SEAT);
					}

					final Player playerInGame = parsePlayerSeat(nextL);
					hand.addPlayer(playerInGame);

					if (LOGGER.isDebugEnabled()) {
						LOGGER.debug("Button Seat is {} and PlayerSeat is {}", hand.getButtonSeat(),
								playerInGame.getSeat());
					}
					if (hand.getButtonSeat().equals(playerInGame.getSeat())) {
						if (LOGGER.isDebugEnabled()) {
							LOGGER.debug("Button Player is {} in seat {}", playerInGame.getName(),
									playerInGame.getSeat());
						}
						hand.setDealerPlayer(playerInGame);
					}
					nextL = nextLine(input);
				}
				if (nextL.contains("posts small blind")) {
					final String[] smallBlindTab = nextL.split(SPACE);
					final String smallBlindPlayer = this.getPlayerBlind(smallBlindTab).replace(COLON, EMPTY);
					final String smallBlind = smallBlindTab[smallBlindTab.length - 1];
					if (LOGGER.isDebugEnabled()) {
						LOGGER.debug("small - player : {}, blind {}, length {}", smallBlindPlayer, smallBlind,
								smallBlindTab.length);
					}
					hand.setSmallBlindPlayer(hand.getPlayersByName().get(smallBlindPlayer));
					nextL = nextLine(input);
				}

				if (nextL.contains("posts big blind")) {
					final String[] bigBlindTab = nextL.split(SPACE);
					final String bigBlindPlayer = this.getPlayerBlind(bigBlindTab).replace(COLON, EMPTY);
					final String bigBlind = bigBlindTab[bigBlindTab.length - 1];
					hand.setBigBlindPlayer(hand.getPlayersByName().get(bigBlindPlayer));
					if (LOGGER.isDebugEnabled()) {
						LOGGER.debug("big - player : {}, blind {}, length {}", bigBlindPlayer, bigBlind,
								bigBlindTab.length);
					}
					nextL = nextLine(input);
				}
				if (nextL.startsWith(HOLE_CARDS)) {

					break;
				}
			}
		}
		return nextL;
	}

	@Override
	public String parseAntesAndBlinds(String nextLine, Scanner input, String phase, String[] nextPhases,
			InfoSession game, Hand hand) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String parseDealer(String nextLine, Scanner input, String phase, String[] nextPhases, Hand hand) {
		String nextL = nextLine;
		if (nextL.startsWith(HOLE_CARDS)) {
			if (LOGGER.isDebugEnabled()) {
				LOGGER.debug(HOLE_CARDS);
			}
			while (input.hasNext()) {

				nextL = nextLine(input);
				if (nextL.startsWith("Dealt")) {

					final int crochetouvrant = nextL.lastIndexOf(OPENNING_SQUARE_BRACKET);

					final String player = nextL.substring("Dealt to ".length(), crochetouvrant - 1);

					final Card[] cartes = parseCards(nextL);
					hand.getMapPlayerCards().put(player, cartes);
					hand.setPlayer(hand.getPlayersByName().get(player));
					if (LOGGER.isDebugEnabled()) {
						LOGGER.debug("player : {}, cards {}", player, this.parseCards(nextL));
					}
					nextL = nextLine(input);
				}
				if (nextL.startsWith(FLOP) || nextL.startsWith(SUMMARY)) {
					break;
				} else {
					if (LOGGER.isDebugEnabled()) {
						LOGGER.debug("dealt to else nextLine {}", nextL);
					}
					final Action action = this.parseAction(nextL, hand.getPlayersByName());
					action.setPhase(Round.PRE_FLOP);
					if (action != null) {
						hand.getPreflopActions().add(action);
					}

				}
			}
		}
		return nextL;
	}

	@Override
	public String parseActionsByPhase(String nextLine, Scanner input, Hand hand, String phase, String[] nextPhases,
			List<Action> actions) {
		String nextL = nextLine;

		if (nextL.startsWith(phase)) {
			if (LOGGER.isDebugEnabled()) {
				LOGGER.debug(phase);
			}
			while (input.hasNext()) {
				nextL = nextLine(input);
				if (startsWith(nextL, nextPhases)) {
					break;
				} else {
					Round round = null;

					switch (phase) {
					case HOLE_CARDS:
						round = Round.PRE_FLOP;
						break;
					case FLOP:
						round = Round.FLOP;
						hand.setFlop(parseCards(nextLine));
						break;
					case TURN:
						round = Round.TURN;
						hand.setTurn(parseCards(nextLine)[0]);
						break;
					case RIVER:
						round = Round.RIVER;
						hand.setRiver(parseCards(nextLine)[0]);
						break;
					case SHOW_DOWN:
						round = Round.SHOWDOWN;
						break;
					default:
						round = null;
					}
					final Action action = this.parseAction(nextL, hand.getPlayersByName());

					if (action != null) {
						action.setPhase(round);
						actions.add(action);
					}
				}
			}
		}
		return nextL;
	}

	@Override
	public String parsePreflop(String nextLine, Scanner input, Hand hand) {
		return nextLine;
	}

	@Override
	public String parseFlop(String nextLine, Scanner input, Hand hand) {
		return parseActionsByPhase(nextLine, input, hand, FLOP, new String[] { TURN, SUMMARY }, hand.getFlopActions());
	}

	@Override
	public String parseTurn(String nextLine, Scanner input, Hand hand) {
		return parseActionsByPhase(nextLine, input, hand, TURN, new String[] { RIVER, SUMMARY }, hand.getTurnActions());
	}

	@Override
	public String parseRiver(String nextLine, Scanner input, Hand hand) {
		return parseActionsByPhase(nextLine, input, hand, RIVER, new String[] { SHOW_DOWN, SUMMARY },
				hand.getRiverActions());
	}

	@Override
	public String parseShowdown(String nextLine, Scanner input, Hand hand) {
		return parseActionsByPhase(nextLine, input, hand, SHOW_DOWN, new String[] { SUMMARY },
				hand.getShowdownActions());
	}

	@Override
	public String parseSummary(String nextLine, Scanner input, InfoSession session, String phase, String[] nextPhases,
			Hand hand) {
		if (nextLine.startsWith(phase)) {
			if (LOGGER.isDebugEnabled()) {
				LOGGER.debug(SUMMARY);
			}
			Player player = null;
			while (input.hasNext()) {

				// Total pot 180 | No rake
				if (nextLine.startsWith("Total pot ")) {

					// Total pot �3.45 Main pot �2.38. Side pot �0.86. |
					// Rake �0.21
					hand.setTotalPot(parseTotalPot(nextLine));
					hand.setRake(parseRake(nextLine));
				}

				if (nextLine.startsWith(BOARD)) {
					if (LOGGER.isDebugEnabled()) {
						LOGGER.debug("Board nextLine {}", nextLine);
					}
					this.parseCards(nextLine);

				}

				if (startsWith(nextLine, nextPhases)) {
					break;
				} else {
					nextLine = input.nextLine();
				}

				if (nextLine.startsWith(SEAT)) {
					player = parsePlayerSummary(nextLine);
					if (player.getCards() != null) {
						hand.getMapPlayerCards().put(player.getName(), player.getCards());
					}
					if (LOGGER.isDebugEnabled()) {
						LOGGER.debug("Seat and bracket {}", nextLine);
					}

				}
			}
			hand.getActions().addAll(hand.getPreflopActions());
			hand.getActions().addAll(hand.getFlopActions());
			hand.getActions().addAll(hand.getTurnActions());
			hand.getActions().addAll(hand.getRiverActions());
			hand.getActions().addAll(hand.getShowdownActions());
			// game.addHand(hand);
		}
		return nextLine;
	}

	@Override
	public Action parseAction(String chaine, Map<String, Player> players) {
		if (LOGGER.isDebugEnabled()) {
			LOGGER.debug("read : {}", chaine);
		}
		final String[] tab = chaine.split(SPACE);
		String action = "";
		String player = "";
		String between = "";
		String amount = "0";
		Card[] hand = null;

		for (int i = 0; i < tab.length; i++) {
			if (EAction.FOLDS.getValue().equals(tab[i]) || EAction.CALLS.getValue().equals(tab[i])
					|| EAction.RAISES.getValue().equals(tab[i]) || EAction.CHECKS.getValue().equals(tab[i])
					|| EAction.COLLECTED.getValue().equals(tab[i]) || EAction.BETS.getValue().equals(tab[i])
					|| EAction.SHOWS.getValue().equals(tab[i]) || "has".equals(tab[i])) {
				player = "";

				action = tab[i];

				if (EAction.CALLS.getValue().equals(tab[i]) || EAction.RAISES.getValue().equals(tab[i])
						|| EAction.COLLECTED.getValue().equals(tab[i]) || EAction.BETS.getValue().equals(tab[i])) {
					amount = tab[i + 1];
					amount = amount.replace(money.getSymbol(), EMPTY);
				}

				for (int j = 0; j < i; j++) {
					if (j == 0) {
						between = "";
					} else {
						between = SPACE;
					}
					player = player + between + tab[j];

				}
				if (EAction.SHOWS.getValue().equals(tab[i])) {
					hand = parseCards(chaine);
				}

			}
		}
		player = player.replace(COLON, EMPTY);

		if (LOGGER.isDebugEnabled()) {
			LOGGER.debug("player {},action {}", player, action);
		}

		if ("has".equals(action) || "".equals(action)) {
			return null;
		} else {
			if (LOGGER.isDebugEnabled()) {
				LOGGER.debug("actionread : {}", action);
			}
			if (players.get(player) == null) {
				LOGGER.debug(chaine);
			}

			return new Action(players.get(player), EAction.valueOf(action.toUpperCase()), Double.parseDouble(amount),
					hand);
		}
	}

	@Override
	public String getTournamentId() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public InfoSession parsing() {
		final InfoSession infoSession = new InfoSession();
		infoSession.setCardRoom(cardRoom);
		Hand hand = null;
		final Map<String, Hand> mapHands = new HashMap<>();

		final Map<String, StringBuffer> mapHandsText = fileToMap();
		StringBuffer buffer;
		for (final String key : mapHandsText.keySet()) {
			buffer = mapHandsText.get(key);
			hand = textToHandDto(buffer, infoSession);

			mapHands.put(hand.getLabel(), hand);

		}
		infoSession.setHands(mapHands);
		return infoSession;
	}

	@Override
	public Double parseBuyIn(String chaine) {
		final String[] tab = chaine.split(SPACE);
		final String buyIn = tab[5];
		int startPosition = 0;
		final int endPosition = buyIn.indexOf(PLUS);
		if (buyIn.contains(money.getSymbol())) {
			startPosition = buyIn.indexOf(money.getSymbol()) + 1;
		} else {
			startPosition = 0;
		}

		if ("Freeroll".equals(buyIn)) {
			return 0d;
		}
		if (buyIn.contains(PLUS)) {
			final String realBuyIn = buyIn.substring(startPosition, endPosition);
			final String fee = buyIn.substring(buyIn.lastIndexOf(PLUS) + 2, buyIn.length());
			if (LOGGER.isDebugEnabled()) {
				LOGGER.debug("buyin {}, money_symbol {}", realBuyIn, money.getSymbol());
			}
			return Double.parseDouble(realBuyIn) + Double.parseDouble(fee);
		}
		return Double.parseDouble(buyIn);
	}

	@Override
	public Double parseFee(String chaine) {
		final String[] tab = chaine.split(SPACE);
		final String buyIn = tab[5];
		int startPosition = 0;
		final int endPosition = buyIn.indexOf(PLUS);

		if (buyIn.contains(money.getSymbol())) {
			startPosition = buyIn.indexOf(money.getSymbol()) + 1;
		} else {
			startPosition = 0;
		}
		if ("Freeroll".equals(buyIn)) {
			return 0d;
		}
		if (buyIn.contains(PLUS)) {
			final String realBuyIn = buyIn.substring(startPosition, endPosition);
			final String fee = buyIn.substring(buyIn.lastIndexOf(PLUS) + 2, buyIn.length());

			if (LOGGER.isDebugEnabled()) {
				LOGGER.debug("buyin {}, fee {}, money_symbol {}", realBuyIn, fee, money.getSymbol());
			}
			return Double.parseDouble(fee);
		}
		return 0d;
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
		final int startPosition = chaine.indexOf("Hand #") + "Hand #".length();
		final int endPosition = chaine.indexOf(COLON);
		return chaine.substring(startPosition, endPosition);
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
		final int startPosition = chaine.indexOf(APOSTROPHE) + 1;
		final int endPosition = chaine.lastIndexOf(APOSTROPHE);
		final String sousChaine = chaine.substring(startPosition, endPosition);
		final String[] tab = sousChaine.split(SPACE);
		return tab[1];
	}

	@Override
	public String parseNumberOfPlayerByTable(String chaine) {
		final int startPosition = chaine.lastIndexOf(APOSTROPHE) + 2;
		final int endPosition = chaine.indexOf("max") - 1;
		return chaine.substring(startPosition, endPosition);
	}

	@Override
	public String parseGameIdSite(String chaine) {
		final int startPosition = chaine.lastIndexOf(HASHTAG) + 1;
		final int endPosition = chaine.indexOf(COMMA);
		return chaine.substring(startPosition, endPosition);
	}

	@Override
	public Integer parseButtonSeat(String chaine) {
		final int startPosition = chaine.indexOf(HASHTAG) + 1;
		final int endPosition = chaine.indexOf("is the button") - 1;
		return Integer.parseInt(chaine.substring(startPosition, endPosition));
	}

	@Override
	public Player parsePlayerSeat(String chaine) {
		final int espace = chaine.indexOf(SPACE);
		final int deuxpoints = chaine.indexOf(COLON);
		final int parenthesegauche = chaine.indexOf(LEFT_PARENTHESIS);
		final int inchips = chaine.indexOf(" in chips)");

		final String seat = chaine.substring(espace + 1, deuxpoints);
		final String player = chaine.substring(deuxpoints + 2, parenthesegauche - 1);
		String stack = chaine.substring(parenthesegauche + 1, inchips);
		stack = stack.replace(money.getSymbol(), EMPTY);
		if (LOGGER.isDebugEnabled()) {
			LOGGER.debug("seat : " + seat + ", player : " + player + ", stack : " + stack + ".");
		}
		final Player playerDTO = new Player(cardRoom, player);
		playerDTO.setStack(Double.parseDouble(stack));
		playerDTO.setSeat(Integer.parseInt(seat));
		playerDTO.setOn(true);

		return playerDTO;
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
		Pattern pattern;
		Matcher matcher;

		if (fileName == null || EMPTY.equals(fileName)) {
			return null;
		} else {

			pattern = Pattern.compile("HH[0-9]{8} T[0-9]{8}");
			matcher = pattern.matcher(fileName);
			if (matcher.find()) {
				return GameType.TOURNAMENT;
			}
			return GameType.CASH;
		}
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
			return DateUtils.toDate(chaine, " yyyy/MM/dd HH:mm:ss z");
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
		//TODO simplify using Actions.useful
		return (line.endsWith(Actions.WILL_BE_ALLOWED_TO_PLAY_AFTER_THE_BUTTON.getValue())
				|| line.contains(Actions.POSTS_SMALL_ET_BIG_BLINDS.getValue())
				|| line.contains(Actions.POSTS_THE_ANTE.getValue()) || line.endsWith(Actions.SITS_OUT.getValue())
				|| line.endsWith(Actions.LEAVES_THE_TABLE.getValue())
				|| line.endsWith(Actions.IS_SITTING_OUT.getValue()) || line.endsWith(Actions.IS_DISCONNECTED.getValue())
				|| line.endsWith(Actions.IS_CONNECTED.getValue()) || line.contains(Actions.SAID.getValue())
				|| line.endsWith(Actions.HAS_TIMED_OUT.getValue())
				|| line.contains(Actions.JOINS_THE_TABLE_AT_SEAT.getValue())
				|| line.contains(Actions.UNCALLED_BET.getValue()) || line.endsWith(Actions.HAS_RETURNED.getValue())
				|| line.contains(Actions.DOESNT_SHOW_HAND.getValue())
				|| line.endsWith(Actions.WAS_REMOVED_FROM_THE_TABLE_FOR_FAILING_TO_POST.getValue())
				|| line.endsWith(Actions.MUCKS_HAND.getValue())
				|| line.contains(Actions.FINISHED_THE_TOURNAMENT_IN.getValue())) && (!line.startsWith("Seat"));
	}

	@Override
	public Map<String, StringBuffer> fileToMap() {
		Scanner input;
		try {
			input = new Scanner(this.getFileToParse(), ENCODING);
		} catch (final IOException e) {
			LOGGER.error(e.getMessage(), e);
			return null;
		}

		if (LOGGER.isDebugEnabled()) {
			LOGGER.debug("The encoding used to read this file is {}.", ENCODING);
			LOGGER.debug("The currency used : {}.", money.name());
		}
		String nextLine = null;

		boolean test = true;

		if (LOGGER.isDebugEnabled()) {
			LOGGER.debug("Starting parsing line by line.");
		}

		final Map<String, StringBuffer> mapHandsText = new HashMap<String, StringBuffer>();
		StringBuffer handText = null;
		String handIdLine = new String();
		boolean firstHand = true;
		while (input.hasNext()) {

			if (test) {
				nextLine = nextLine(input);
				test = false;
			}
			if (nextLine.startsWith(UTF8_BOM)) {
				if (LOGGER.isDebugEnabled()) {
					LOGGER.debug("This character {} has been found and ignored.", UTF8_BOM);
				}
				nextLine = nextLine.substring(1);
			}
			// Demarrage de la lecture d'une main
			if (nextLine.startsWith(NEW_HAND)) {
				if (firstHand) {
					handText = new StringBuffer(nextLine);
					firstHand = false;
				} else {
					mapHandsText.put(handIdLine, handText);
					handText = new StringBuffer(nextLine);
				}
				handIdLine = nextLine;

				handText = handText.append(EOL);

			} else {
				if (!"".equals(nextLine)) {
					handText = handText.append(nextLine);
					handText = handText.append(EOL);
				}
			}

			nextLine = nextLine(input);

		}
		mapHandsText.put(handIdLine, handText);
		input.close();
		return mapHandsText;
	}

	@Override
	public Hand textToHandDto(StringBuffer text, InfoSession infoSession) {
		final Scanner input = new Scanner(text.toString());
		final Hand hand = new Hand();

		if (LOGGER.isDebugEnabled()) {
			LOGGER.debug("The encoding used to read this file is {}.", ENCODING);
			LOGGER.debug("The currency used : {}.", money.name());
		}
		String nextLine = input.nextLine();
		// Demarrage de la lecture d'une main
		if (nextLine.startsWith(NEW_HAND)) {

			nextLine = parseNewHandLine(nextLine, input, NEW_HAND, null, infoSession, hand);
		}
		nextLine = nextLine(input);

		nextLine = parseTableLine(nextLine, input, TABLE, null, infoSession, hand);
		nextLine = nextLine(input);

		nextLine = parseSeatLine(nextLine, input, SEAT, new String[] { HOLE_CARDS }, infoSession, hand);

		// Renommer cette methode
		nextLine = parseDealer(nextLine, input, HOLE_CARDS, new String[] { FLOP, SUMMARY }, hand);
		// Lecture des actions du coup
		nextLine = parsePreflop(nextLine, input, hand);

		nextLine = parseFlop(nextLine, input, hand);

		nextLine = parseTurn(nextLine, input, hand);

		nextLine = parseRiver(nextLine, input, hand);

		nextLine = parseShowdown(nextLine, input, hand);

		nextLine = parseSummary(nextLine, input, infoSession, SUMMARY, new String[] { NEW_HAND }, hand);

		hand.setCardRoom(infoSession.getCardRoom());
		return hand;
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
