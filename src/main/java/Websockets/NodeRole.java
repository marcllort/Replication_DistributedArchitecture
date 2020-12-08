package Websockets;

public enum NodeRole {
	A1(6010, 3050),
	A2(6011, 3051),
	A3(6012, 3052),
	B1(6110, 3053),
	B2(6111, 3054),
	C1(6210, 3055),
	C2(6211, 3056);

	private final int port;
	private final int wsPort;

	NodeRole(int port, int wsPort) {
		this.port = port;
		this.wsPort = wsPort;
	}

	public int getPort() {
		return port;
	}

	public int getWsPort() {
		return wsPort;
	}

	public NodeRole[] getBroadcastNodes() {
		switch (this) {
			case A1:
				return new NodeRole[]{A2, A3};
			case A2:
				return new NodeRole[]{A1, A3};
			case A3:
				return new NodeRole[]{A1, A2};

			case B1:
				return new NodeRole[]{B2};
			case B2:
				return new NodeRole[]{B1};

			case C1:
				return new NodeRole[]{C2};
			case C2:
				return new NodeRole[]{C1};

			default:
				return new NodeRole[]{};
		}
	}

	public int[] getBroadcastAddresses() {
		switch (this) {
			case A1:
				return new int[]{A2.port, A3.port};
			case A2:
				return new int[]{A1.port, A3.port};
			case A3:
				return new int[]{A1.port, A2.port};

			case B1:
				return new int[]{B2.port};
			case B2:
				return new int[]{B1.port};

			case C1:
				return new int[]{C2.port};
			case C2:
				return new int[]{C1.port};
		}

		return new int[]{};
	}

	public static NodeRole[] getArray() {
		return new NodeRole[]{A1, A2, A3, B1, B2, C1, C2};
	}

	@Override
	public String toString() {
		return this.name();
	}
}