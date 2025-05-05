package Card;

public enum CardType {
    ADMIN,
    VOTER,
    UNKNOWN;

    public static CardType resolve(String cardId) {
        if (cardId != null && cardId.length() == 9) {
            String prefix = cardId.substring(0, 1);
            String digits = cardId.substring(1);

            if (digits.matches("\\d{8}")) {
                if (prefix.equals("A")) return ADMIN;
                if (prefix.equals("V")) return VOTER;
            }
        }
        return UNKNOWN;
    }
}
