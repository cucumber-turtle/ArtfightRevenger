package Persistency;

import Client.AttackInfo;
import java.io.FileWriter;
import java.io.IOException;

/**
 * Record revenge data to a CSV file.
 *
 * @author cucumber 2022
 */
public class Record {
  private static final String FILE_EXTENSION = ".csv";
  private final String filePath;

  /**
   * Create a new file and initialise with the column titles.
   *
   * @param filePath Path to the file.
   */
  public Record (String filePath) {
    this.filePath = filePath;
    try {
      FileWriter writer = new FileWriter(this.filePath + FILE_EXTENSION);
      writer.write("Attacker username,Attack title,Attack link,Attacker profile,Previous attack\n");
      writer.close();
    } catch (IOException e) {
      e.printStackTrace();
    }
  }

  /**
   * Append a row to an existing revenge file.
   *
   * @param filePath Path to the file.
   * @param revenge Revenge data to write to file.
   */
  public Record (String filePath, AttackInfo revenge) {
    this.filePath = filePath;
    addRevenge(revenge);
  }

  /**
   * Append a row to the revenge file.
   *
   * @param revenge Revenge data to write to file.
   */
  public void addRevenge (AttackInfo revenge) {
    try {
      FileWriter writer = new FileWriter(this.filePath + FILE_EXTENSION);
      writer.append(revenge.getAttacker()).append(",").append(revenge.getAttackName())
          .append(",").append(revenge.getAttackLink()).append(",").append(revenge.getAttackerLink())
          .append(",").append(revenge.getPrevAttack()).append("\n");
      writer.close();
    } catch (IOException e) {
      e.printStackTrace();
    }
  }

}