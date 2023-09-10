import org.junit.jupiter.api.Test;

public class TestRecord {
    @Test
    public void testRecord() {
        Record record = new Record("test-record");
        AttackInfo attack = new AttackInfo("attacker", "attack", "attack-link", "attacker-link", "prev-attack");
        record.addRevenge(attack);
        File file = new File("test-record.csv");
        assertTrue(file.exists());
        file.delete();
    }
}