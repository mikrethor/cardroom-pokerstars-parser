package fr.mikrethor.cardroom.parser;

import java.nio.file.Path;

/**
 * Cashgame pokerstars parser.
 * 
 * @author Thor
 * 
 */
public class PokerstarsParserCashGame extends PokerstarsParser implements ICardroomParser {

	public PokerstarsParserCashGame(Path fileToParse) {
		super(fileToParse);
	}

	@Override
	public String parseTableId(String chaine) {
		final int startPosition = chaine.indexOf(APOSTROPHE) + 1;
		final int endPosition = chaine.lastIndexOf(APOSTROPHE);
		final String sousChaine = chaine.substring(startPosition, endPosition);
		return sousChaine;
	}

	@Override
	public String getTournamentId() {
		final String fileName = this.getFileToParse().getFileName().toString();
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

	// play money not handle
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
