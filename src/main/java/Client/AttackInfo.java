package Client;

import static org.junit.jupiter.api.Assertions.assertNotNull;

/**
 * @author cucumber 2022
 */
public class AttackInfo {
  private final String attackName;
  private final String attacker;
  private final String attackLink;
  private String nextAttack;

  public AttackInfo (String attackName, String attacker, String attackLink) {
    assertNotNull(attackName);
    assertNotNull(attacker);
    assertNotNull(attackLink);

    this.attackName = attackName;
    this.attacker = attacker;
    this.attackLink = attackLink;
    this.nextAttack = null;
  }

  public void setNextAttack (String nextAttack) {
    this.nextAttack = nextAttack;
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

  public String getNextAttack() {
    return nextAttack;
  }
}
