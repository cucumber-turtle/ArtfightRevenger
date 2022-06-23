package Client;

import static org.junit.jupiter.api.Assertions.assertNotNull;

/**
 * @author cucumber 2022
 */
public class AttackInfo {
  private final String attackName;
  private final String attacker;
  private final String attackLink;
  private String prevAttack;

  public AttackInfo (String attackName, String attacker, String attackLink) {
    assertNotNull(attackName);
    assertNotNull(attacker);
    assertNotNull(attackLink);

    this.attackName = attackName;
    this.attacker = attacker;
    this.attackLink = attackLink;
    this.prevAttack = null;
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
}
