package Content;

public class Card {
    public String cardName;
    public String cardPin;
    public String cardType;
    public String cardBalance;

    public Card(String cardName, String cardPin, String cardBalance) {
        this.cardName = cardName;
        this.cardPin = cardPin;
        this.cardBalance = cardBalance;
    }

    public Card() {
    }

    public String getCardName() {
        return cardName;
    }

    public String getCardPin() {
        return cardPin;
    }

    public String getCardType() {
        return cardType;
    }

    public String getCardBalance() {
        return cardBalance;
    }
}
