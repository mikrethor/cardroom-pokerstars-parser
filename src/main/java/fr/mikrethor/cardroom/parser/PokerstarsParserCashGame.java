package fr.mikrethor.cardroom.parser;

import java.io.File;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Parsing Winamax mano. Fonctionne mais dur emaintenir. Piste regex en cours
 * d'etude.
 * 
 * D:\Profiles\Thor\Documents\Winamax Poker\accounts\Mikrethor\history
 * 
 * @author Thor
 * 
 */
public class PokerstarsParserCashGame extends PokerstarsParser implements ICardroomParser {

	/**
	 * LOGGER.
	 */
	private static final Logger LOGGER = LoggerFactory.getLogger(PokerstarsParserCashGame.class);

	public PokerstarsParserCashGame(File fileToParse) {
		super(fileToParse);
	}

	@Override
	public String parseTableId(String chaine) {
		final int startPosition = chaine.indexOf(APOSTROPHE) + 1;
		final int endPosition = chaine.lastIndexOf(APOSTROPHE);
		final String sousChaine = chaine.substring(startPosition, endPosition);
		// final String[] tab = sousChaine.split(ESPACE);
		return sousChaine;
	}

	@Override
	public String getTournamentId() {
		// HH20141023 Endeavour II - 0,02�$-0,05�$ - USD Hold'em No Limit
		final String fileName = this.getFileToParse().getName();
		final String id = fileName.substring(fileName.indexOf(SPACE) + 1, fileName.indexOf(DASH));
		return id;
	}

	@Override
	public String parseGameIdSite(String chaine) {
		return "CashGame";
	}

	@Override
	public Double parseBuyIn(String chaine) {
		return 0d;
	}

	@Override
	public Double parseFee(String chaine) {
		return 0d;
	}

	// Le cas play money n'est pas gere
	@Override
	public Double parseSmallBlind(String chaine) {
		final int startPosition = chaine.indexOf(money.getSymbol()) + 1;
		final int endPosition = chaine.indexOf(SLASH);
		String smallBlind = chaine.substring(startPosition, endPosition);
		smallBlind = smallBlind.replace(money.getSymbol(), EMPTY);
		return Double.parseDouble(smallBlind);
	}

	@Override
	public Double parseBigBlind(String chaine) {
		final int startPosition = chaine.indexOf(SLASH) + 2;
		final int endPosition = chaine.indexOf(money.getShortName());
		String bigBlind = chaine.substring(startPosition, endPosition);
		bigBlind = bigBlind.replace(money.getSymbol(), EMPTY);
		return Double.parseDouble(bigBlind);
	}

}
