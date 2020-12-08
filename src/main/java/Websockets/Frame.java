package Websockets;

import java.io.Serializable;

/**
 * Classe per a transmetre trames entre sockets.
 * <p>
 * Conté un Type que identifica la trama que és i un Object per a recuperar la informació transmesa, que pot variar
 * segons Type.
 *
 * @author Ajordat
 * @version 1.0
 **/
public class Frame implements Serializable {
	private Type type;
	private Object data;

	/**
	 * Diferent tipus de trames que es poden transmetre.
	 */
	public enum Type {
		REQUEST_CLIENT,
		REPLY_CLIENT,

		POST_AA,
		REPLY_AA,

		POST_AB,
		REPLY_BA,

		POST_BC,
		REPLY_CB,

		REPLY_KO
	}

	public Frame(Type type, Object data) {
		this.type = type;
		this.data = data;
	}

	public Frame(Type type) {
		this.type = type;
		this.data = null;
	}

	public Type getType() {
		return this.type;
	}

	public Object getData() {
		return data;
	}

}
