package relogioLogico;

public class RegiaoCritica implements Runnable {
  static int i = 0;
  private int seconds;
  private Processo owner;
  private long id;

  public RegiaoCritica(Processo processo, int seconds) {
    this.owner = processo;
    this.seconds = seconds; 
    this.id = Thread.currentThread().getId();
  }

  @Override
  public void run () {
    try {
      System.out.println(System.currentTimeMillis() + " Processo acessando regi�o critica : IdProcesso" + this.id  + " - I - " + RegiaoCritica.i);
      Thread.sleep(seconds * 1000);
      RegiaoCritica.i++;
      System.out.println(System.currentTimeMillis() + " Processo saindo regi�o critica : IdProcesso " + this.id + " - I - " + RegiaoCritica.i);
      this.owner.sairRegiaoCritica();
    } catch (Exception ex) {
      ex.printStackTrace();
    }
  }
}