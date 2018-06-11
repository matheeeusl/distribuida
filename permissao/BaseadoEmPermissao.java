package permissao;

import java.util.LinkedList;
import java.util.Queue;

public class BaseadoEmPermissao {
	
	static boolean token;
   
    //fila de itens
    static LinkedList<MeuProcesso> processos = new LinkedList<MeuProcesso>();
    
    static Queue<MeuProcesso> processoEsperandoAcesso = new LinkedList<MeuProcesso>();
    
    //construtor
    /**********************************************************/
    BaseadoEmPermissao(int size) { 
    	token = true;
    	//p�e os processo na fila
        for (int i = 0; i < size; i++) {
        	//cria o processo
            MeuProcesso processo = new MeuProcesso(i);
            //inicia o processo
            processo.start();
            //adiciona o processo na fila
            processos.add(processo);   
        }
  	
    }  
    
    //requisi��o de acesso a regi�o critica
    static void requestAccessCritialArea(MeuProcesso n) {
    	//concede o acesso se a fila estiver vazia
    	if(processoEsperandoAcesso.isEmpty() && token) {
    		token = false;
    		n.conceedAccessCritialArea(token);
    	} else {
    		//adiciona o nodo a fila caso n�o esteja com o token
    		processoEsperandoAcesso.add(n);
    	}
    }
    
    //devolu��o do token pelo processo
    /**********************************************************/
    static void backToken(boolean t) {
    	//recebe o token
    	token = t;
    	//verifica se tem nodos na fila
    	if(!processoEsperandoAcesso.isEmpty()) {
    		///pega o proximo nodo da fila
    		MeuProcesso next = processoEsperandoAcesso.poll();
    		//concede o token para este nodo
    		next.conceedAccessCritialArea(token);
    	}
    }
    
    //representa��o da regi�o cr�tica
    /**********************************************************/
    static void criticalArea() throws Exception {
    	Thread.sleep(5000);
    }
    
    /**
     *     Processo que vai na fila
     *
     */
    
    /**********************************************************/
    static class MeuProcesso extends Thread {

    	boolean token;
        int index;
        boolean isAccessRequested;
   
        //construtor
        /**********************************************************/
        MeuProcesso(int i) {
            this.index = i;
            this.token = false;
            this.isAccessRequested = false;
            System.out.println("[Nodo " + this.index + "] iniciado");
        }

        //tentativa de acesso a regi�o cr�tica
        /**********************************************************/
        private void requestAccessCritialArea() throws Exception {
        	this.isAccessRequested = true;
        	System.out.println("[Nodo " + this.index + "] Requisitando acesso a RC");
        	BaseadoEmPermissao.requestAccessCritialArea(this);
        }
        
        //permiss�o de acesso a regi�o cr�tica
        /**********************************************************/
        public void conceedAccessCritialArea(boolean token) {
        	this.token = token; 
        	System.out.println("[Nodo " + this.index + "] Acesso concedido a RC");
        	//acessa a RC
        	try {
        		BaseadoEmPermissao.criticalArea();
        	} catch (Exception e) {
        		System.err.println("[Nodo " + this.index + "] Erro na execu��o");
        	}
    		//devolve o token
    		System.out.println("[Nodo " + this.index + "] devolvendo token");
    		BaseadoEmPermissao.backToken(this.token);
        }
        
        //inicia o processo
        /**********************************************************/
        public void run() {
        	try {
        		this.sleep(1000);
        		
        		//requisita o acesso a RC caso ainda nao tenha requisitado
        		if(!isAccessRequested) this.requestAccessCritialArea();
        		
        	} catch (Exception e) {
        		System.err.println( "[Nodo " + this.index + "]Erro na execu��o");
        	}
        }
    }

}
