package relogioLogico;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Random;

public class RelogioLogico {
	public RelogioLogico(int n) {
		int porta = 5001;
		String host = "127.0.0.1";
		Random rand = new Random();
		ArrayList<Processo> processos = new ArrayList<Processo>();
		
		try {
			for (int i = 1 ; i <= n ; i++)
				processos.add(new Processo(host, porta, (rand.nextInt(100) + 30), i, n));
			for (Processo processo : processos)
				new Thread(processo).start();
		}
		catch (IOException e) {
			e.printStackTrace();
		}
	}

}
