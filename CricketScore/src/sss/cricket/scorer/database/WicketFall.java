package sss.cricket.scorer.database;

import sss.cricket.scorer.ui.ScoreSheetDataStore;

public class WicketFall {

	long inningsId;

	long batsmanId;

	long bowlerId;

	int wicketType;

	long fielderId;
	
	public long getFielderId() {
		return fielderId;
	}

	public void setFielderId(long fielderId) {
		this.fielderId = fielderId;
	}

	public long getInningsId() {
		return inningsId;
	}

	public void setInningsId(long inningsId) {
		this.inningsId = inningsId;
	}

	public long getBatsmanId() {
		return batsmanId;
	}

	public void setBatsmanId(long batsmanId) {
		this.batsmanId = batsmanId;
	}

	public long getBowlerId() {
		return bowlerId;
	}

	public void setBowlerId(long bowlerId) {
		this.bowlerId = bowlerId;
	}

	public int getWicketType() {
		return wicketType;
	}

	public void setWicketType(int wicketType) {
		this.wicketType = wicketType;
	}

	public String getWicketDetail() {
		String fielderName = ScoreSheetDataStore.getPlayerName(fielderId);
		String bowlerName = ScoreSheetDataStore.getPlayerName(bowlerId);

		switch (wicketType) {
		case 1: // Caught
			return "c " + fielderName + " b " + bowlerName;
		case 2: // bowled
			return "b " + bowlerName;
		case 3:
			return "runout " + fielderName;
		case 4:
			return "Stumped " + fielderName + " b " + bowlerName;
		case 5:
			return "c & b " + bowlerName;
		case 6:
			return "LBW, b " + bowlerName;
		case 7:
			return "Hit out, b " + bowlerName;
		case 8:
			return "Handled ball, b " + bowlerName;
		default:
			return "Other, b" + bowlerName;
		}
	}

}
