package relogioLogico;

import java.io.IOException;
import java.net.*;
import java.util.*;

public class Processo implements Runnable {
	private int Porta, Mult;
	private InetAddress address;
	private MulticastSocket socket;
	private long Tempo, Id;
	private boolean isRegiaoCritica;
	private int numeroProcessos;
	private ArrayList<Mensagem> queue;

	public Processo(String host, int porta, int mult, long contador, int numeroProcessos) throws IOException {
		socket = new MulticastSocket(porta);
		address = InetAddress.getByName(host);
		this.Porta = porta;
		this.Mult = mult;
		this.Tempo = contador;
		this.isRegiaoCritica = false;
		this.numeroProcessos = numeroProcessos;
		socket.joinGroup(address);
		this.queue = new ArrayList<Mensagem>();
	}

	public synchronized void sairRegiaoCritica() {
		for (int i = 0; i < this.queue.size(); i++) {
			try {
				Mensagem Mensagem = this.queue.get(i);
				System.out.println(
						System.currentTimeMillis() + " " + this.Id + " habilitando Mensagem de " + Mensagem.getIdProcesso());
				Mensagem.enableResponse(this.Id);
				socket.send(new DatagramPacket(Mensagem.getBytes(), Mensagem.getBytes().length, address, Porta));
			} catch (IOException ex) {
				ex.printStackTrace();
			}
		}
		this.isRegiaoCritica = false;
		this.queue = new ArrayList<Mensagem>();
	}

	public void run() {
		this.Id = Thread.currentThread().getId();
		System.out.println("Criando processo: " + this.Id + " " + this.Mult);
		Mensagem msg = null;
		ArrayList<Mensagem> responses = null;

		try {
			for (int i = 1; true; i++) {
				responses = EnviarDatagramaSolicitacao(socket, msg, responses, i);
				
				byte[] buff = new byte[4096];
				DatagramPacket receive = new DatagramPacket(buff, buff.length);
				socket.setSoTimeout(200);

				try {
					socket.receive(receive);
				} catch (Exception ex) {
					continue;
				}

				Mensagem mensagem = new Mensagem(receive.getData());
				EnviarMensagem(mensagem, TipoMensagem.Recebido);

				if (mensagem.getIdProcesso() != this.Id) {
					if (mensagem.getIdProcessoResponse() == 0) {
						TrocaMensagensRegiaoCritica(mensagem, msg, responses);
					}
				} else {
					if (mensagem.getIdProcessoResponse() != 0) {
						if (responses != null) {
							responses.add(mensagem);
							if (responses.size() == this.numeroProcessos - 1 && !this.isRegiaoCritica) {
								responses = null;
								this.isRegiaoCritica = true;
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

	private void TrocaMensagensRegiaoCritica(Mensagem mensagem, Mensagem msg, ArrayList<Mensagem> responses)
			throws IOException {
		Tempo = (int) Math.max(Tempo, mensagem.getCount()) + 1;

		if (isForaDaRegiaoCriticaSemReposta(responses)) {
			EnviarMensagem(mensagem, TipoMensagem.Habilitando);
			EnviarDatagramaHabilitar(mensagem);

		} else if (this.isRegiaoCritica) {
			EnviarMensagem(mensagem, TipoMensagem.AdicionarQueue);
			this.queue.add(mensagem);

		} else {
			if (msg.getCount() > mensagem.getCount()) {
				EnviarMensagem(mensagem, TipoMensagem.Habilitando);
				EnviarDatagramaHabilitar(mensagem);

			} else {
				EnviarMensagem(mensagem, TipoMensagem.AdicionarQueue);
				this.queue.add(mensagem);
			}
		}
	}

	private boolean isForaDaRegiaoCriticaSemReposta(ArrayList<Mensagem> responses) {
		return !this.isRegiaoCritica && responses == null;
	}

	private void EnviarDatagramaHabilitar(Mensagem mensagem) throws IOException {
		socket.send(new DatagramPacket(mensagem.getBytes(), mensagem.getBytes().length, address, Porta));
	}

	private ArrayList<Mensagem> EnviarDatagramaSolicitacao(MulticastSocket socket, Mensagem msg, ArrayList<Mensagem> responses, int i)
			throws IOException {
		if ((i % this.Mult) == 0) {
			msg = new Mensagem("Solicitar  " + i, this.Id, Tempo++);
			EnviarMensagem(msg, TipoMensagem.Solicitando);

			byte[] msgB = msg.getBytes();
			responses = new ArrayList<Mensagem>();
			socket.send(new DatagramPacket(msgB, msgB.length, address, Porta));
		}
		return responses;
	}

	private void EnviarMensagem(Mensagem mensagem, TipoMensagem tipoMensagem) {
		switch (tipoMensagem) {
		case Habilitando:
			System.out
					.println(System.currentTimeMillis() + " " + this.Id + " habilitando Mensagem de " + mensagem.getIdProcesso());
			mensagem.enableResponse(this.Id);
			break;
		case AdicionarQueue:
			System.out.println(System.currentTimeMillis() + " " + this.Id + " adicionando na queue " + mensagem);
			break;
		case Recebido:
			System.out.println(System.currentTimeMillis() + " " + this.Id + " recebeu " + mensagem);
			break;
		case Solicitando:
			System.out.println(System.currentTimeMillis() + " " + this.Id + " solicitando " + mensagem);
			break;
		}

	}

}