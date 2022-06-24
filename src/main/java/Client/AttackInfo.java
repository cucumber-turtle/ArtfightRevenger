package Client;

import static org.junit.jupiter.api.Assertions.assertNotNull;

/**
 * @author cucumber 2022
 */
public class AttackInfo {
  private final String attackName;
  private final String attacker;
  private final String attackLink;
  private String attackerLink;
  private String prevAttack;

  public AttackInfo (String attackName, String attacker, String attackLink) {
    assertNotNull(attackName);
    assertNotNull(attacker);
    assertNotNull(attackLink);

    this.attackName = attackName;
    this.attacker = attacker;
    this.attackLink = attackLink;
    // This will not always produce a valid link
    this.attackerLink = "https://artfight.net/~" + attacker;
    this.prevAttack = "";
  }

  public void setPrevAttack (String prevAttack) {
    this.prevAttack = prevAttack;
  }

  public String getAttackName() {
    return attackName;
  }

  public String getAttacker() {
    return attacker;
  }

  public String getAttackLink() {
    return attackLink;
  }

  public String getPrevAttack() {
    return prevAttack;
  }

  public String getAttackerLink() {
    return attackerLink;
  }

  public void setAttackerLink(String attackerLink) {
    this.attackerLink = attackerLink;
  }
}
