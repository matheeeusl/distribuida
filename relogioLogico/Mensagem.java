package relogioLogico;


import java.io.*;
import java.text.MessageFormat;

public class Mensagem implements Serializable {
	private static final long serialVersionUID = 1L;
	
	private long IdProcesso;
	private long IdProcessoResponse;
	private String name;
	private long count;
	
	public Mensagem(byte[] params) throws IOException, ClassNotFoundException {
		ByteArrayInputStream bis = new ByteArrayInputStream(params);
		ObjectInput in = new ObjectInputStream(bis);

		Mensagem newMensagem = (Mensagem) in.readObject();
		this.IdProcesso = newMensagem.IdProcesso;
		this.name = newMensagem.name;
		this.count = newMensagem.count;
		this.IdProcessoResponse = newMensagem.IdProcessoResponse;
	}
	
	public Mensagem(Mensagem Mensagem) {
		this.IdProcesso = Mensagem.IdProcesso;
		this.name = Mensagem.name;
		this.IdProcessoResponse = Mensagem.IdProcessoResponse;
		this.count = Mensagem.count;
	}
	
	public Mensagem (String name, long IdProcesso, long count) {
		this.name = name;
		this.IdProcesso = IdProcesso;
		this.count = count;
		this.IdProcessoResponse = 0;
	}
	
	public String getMensagem() {
		return this.name;
	}
	
	public long getIdProcesso() {
		return this.IdProcesso;
	}
	
	public long getCount() {
		return this.count;
	}
	
	public byte[] getBytes() throws IOException {
		ByteArrayOutputStream bos = new ByteArrayOutputStream();
		ObjectOutput out = new ObjectOutputStream(bos);   
		out.writeObject(this);
		out.flush();
		bos.close();
		return bos.toByteArray();
	}
	
	public void enableResponse(long idProcesso) {
		this.IdProcessoResponse = idProcesso;
	}

	public long getIdProcessoResponse () {
		return this.IdProcessoResponse;
	}

	public String toString() {
		Object[] params = new Object[]{this.name, this.IdProcesso,  this.count, this.IdProcessoResponse};
		return MessageFormat.format("Mensagem: Nome Processo: - {0}\nId Processo: - {1}\nContador: - {2}\nResposta: {3}", params);
	}
}