package devDataType.Parameters;

public class NumericParameter extends Parameter{
	

	public enum DataError{
		NO_ERROR(0x00),
		UNVALID_VALUE (0x01),
		OVER_MAXIMUM (0x02),
		LESS_MINIMUM (0x04);

		private int nCode ;

		private DataError( int nCode) {
		    this.nCode = nCode;
		}

		public int Value(){
			return this.nCode;
		}


		@Override

		public String toString() {

		    return String.valueOf ( this.nCode );

		} 
	}
		
	
		protected DataError error;
	
			
}
