package sss.cricket.scorer.database;

public class MatchType {

	
	public long MatchTypeId;
	public String MatchTypeName;
	public boolean IsLimitedOvers;
	public boolean IsTwoInnigs;
	
	 public String getSpinnerText() {
         return MatchTypeName;
     }

     public String getValue() {
         return String.valueOf(MatchTypeId);
     }

     public String toString() {
         return MatchTypeName;
     }
}
