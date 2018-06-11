package relogioLogico;


import java.io.IOException;
import java.net.*;
import java.util.*;

public class Processo implements Runnable {
	private int Porta, Mult;
	private InetAddress address;
	private MulticastSocket socket;
	private long Tempo, Id;
	private boolean inCritical;
	private int numeroProcessos;
	private ArrayList<Mensagem> queue;
	
	public Processo(String host, int porta, int mult, long contador, int numeroProcessos) throws IOException {
		socket = new MulticastSocket(porta);
		address = InetAddress.getByName(host);
		this.Porta = porta;
		this.Mult = mult;
		this.Tempo = contador;
		this.inCritical = false;
		this.numeroProcessos = numeroProcessos;
		socket.joinGroup(address);
		this.queue = new ArrayList<Mensagem>();
	}

	public synchronized void sairRegiaoCritica() {
		for (int i = 0 ; i < this.queue.size() ; i++) {
			try {
				Mensagem Mensagem = this.queue.get(i);
				System.out.println(System.currentTimeMillis() + " " + this.Id + " habilitando Mensagem de " + Mensagem.getIdProcesso());
				Mensagem.enableResponse(this.Id);
				socket.send(new DatagramPacket(Mensagem.getBytes(), Mensagem.getBytes().length, address, Porta));
			} catch (IOException ex) {
				ex.printStackTrace();
			}
		}
		this.inCritical = false;
		this.queue = new ArrayList<Mensagem>();
	}

	public void run() {
		this.Id = Thread.currentThread().getId();
		System.out.println("Criando processo: " + this.Id + " " + this.Mult);
		Mensagem msg = null;
		ArrayList<Mensagem> responses = null;

		try {
			for (int i = 1; true; i++) {
				if ((i % this.Mult) == 0) {
					msg = new Mensagem("Alo " + i, this.Id, Tempo++);
					System.out.println(System.currentTimeMillis() + " " + this.Id + " solicitando " + msg);
					byte[] msgB = msg.getBytes();
					responses = new ArrayList<Mensagem>();
					socket.send(new DatagramPacket(msgB, msgB.length, address, Porta));
				}

				byte[] buff = new byte[4096];
				DatagramPacket receive = new DatagramPacket(buff, buff.length);
				socket.setSoTimeout(200);

				try {
					socket.receive(receive);
				} catch (Exception ex) {
					continue;
				}
				Mensagem Mensagem = new Mensagem(receive.getData());
				System.out.println(System.currentTimeMillis() + " " + this.Id + " recebeu " + Mensagem);
				if (Mensagem.getIdProcesso() != this.Id) {
					if (Mensagem.getIdProcessoResponse() == 0) {
						Tempo = (int) Math.max(Tempo, Mensagem.getCount()) + 1;
						if (!this.inCritical && responses == null) {
							System.out.println(System.currentTimeMillis() + " " + this.Id + " habilitando Mensagem de " + Mensagem.getIdProcesso());
							Mensagem.enableResponse(this.Id);
							socket.send(new DatagramPacket(Mensagem.getBytes(), Mensagem.getBytes().length, address, Porta));
						} else if (this.inCritical) {
							System.out.println(System.currentTimeMillis() + " " + this.Id + " adicionando na queue " + Mensagem);
							this.queue.add(Mensagem);
						} else {
							if (msg.getCount() > Mensagem.getCount()) {
								System.out.println(System.currentTimeMillis() + " " + this.Id + " habilitando Mensagem de " + Mensagem.getIdProcesso());
								Mensagem.enableResponse(this.Id);
								socket.send(new DatagramPacket(Mensagem.getBytes(), Mensagem.getBytes().length, address, Porta));
							} else {
								System.out.println(System.currentTimeMillis() + " " + this.Id + " adicionando na queue " + Mensagem);
								this.queue.add(Mensagem);
							}
						}
					}
				} else {
					if (Mensagem.getIdProcessoResponse() != 0) {
						if (responses != null) {							
							responses.add(Mensagem);
							if (responses.size() == this.numeroProcessos - 1 && !this.inCritical) {
								responses = null;
								this.inCritical = true;
								new Thread(new RegiaoCritica(this, 2)).start();
							} 
						}
					}
				}
			}
		} catch (IOException | ClassNotFoundException e) {
			e.printStackTrace();
		}
	}
	
}