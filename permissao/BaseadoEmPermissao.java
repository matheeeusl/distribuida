package permissao;

import java.util.LinkedList;
import java.util.Queue;

public class BaseadoEmPermissao {
	
	static boolean token;
   
    //fila de processos
    static LinkedList<Processo> processos = new LinkedList<Processo>();
    
    static Queue<Processo> processoEsperandoAcesso = new LinkedList<Processo>();
    
    BaseadoEmPermissao(int size) { 
    	token = true;
    	//põe os processo na fila
        for (int i = 0; i < size; i++) {
        	//cria o processo
            Processo processo = new Processo(i);
            //inicia o processo
            processo.start();
            //adiciona o processo na fila
            processos.add(processo);   
        }
  	
    }  
    
    //requisição de acesso a região critica
    static void requestAccessCritialArea(Processo n) {
    	//concede o acesso se a fila estiver vazia
    	if(processoEsperandoAcesso.isEmpty() && token) {
    		token = false;
    		n.concedeAcessoRegiaoCritica(token);
    	} else {
    		//adiciona o processo a fila caso não esteja com o token
    		processoEsperandoAcesso.add(n);
    	}
    }
    
    //devolução do token pelo processo
    static void devolveToken(boolean t) {
    	//recebe o token
    	token = t;
    	//verifica se tem processos na fila
    	if(!processoEsperandoAcesso.isEmpty()) {
    		///pega o proximo processo da fila
    		Processo next = processoEsperandoAcesso.poll();
    		//concede o token para este processo
    		next.concedeAcessoRegiaoCritica(token);
    	}else {
    		System.out.println("Todos processos ociosos acessaram a região crítica");
    		System.exit(0);
    	}
    }
    
    //representação da região crítica
    static void regiaoCritica() throws Exception {
    	System.out.println("Região crítica acessada");
    	Thread.sleep(3500);
    }
    
    static class Processo extends Thread {

    	boolean token;
        int index;
        boolean acessoRequisitado;
   
        //construtor
        Processo(int i) {
            this.index = i;
            this.token = false;
            this.acessoRequisitado = false;
            System.out.println("Processo " + this.index + " iniciado");
        }

        //tentativa de acesso a região crítica
        private void requisitarAcessoRC() throws Exception {
        	this.acessoRequisitado = true;
        	System.out.println("Processo " + this.index + " ==> Requisitando acesso a RC");
        	BaseadoEmPermissao.requestAccessCritialArea(this);
        }
        
        //permissão de acesso a região crítica
        public void concedeAcessoRegiaoCritica(boolean token) {
        	this.token = token; 
        	System.out.println("Processo " + this.index + " ==> Acesso concedido a RC");
        	//acessa a RC
        	try {
        		BaseadoEmPermissao.regiaoCritica();
        	} catch (Exception e) {
        		System.err.println("Processo " + this.index + " ==> Erro na execução");
        	}
    		//devolve o token
    		System.out.println("Processo " + this.index + " ==> devolvendo token");
    		BaseadoEmPermissao.devolveToken(this.token);
        }
        
        //inicia a classe
        public void run() {
        	try {
        		this.sleep(1000);
        		//requisita o acesso a RC caso ainda nao tenha requisitado
        		if(!acessoRequisitado) this.requisitarAcessoRC();
        		
        	} catch (Exception e) {
        		System.err.println( "Processo " + this.index + " ==> Erro na execução");
        	}
        }
    }
}