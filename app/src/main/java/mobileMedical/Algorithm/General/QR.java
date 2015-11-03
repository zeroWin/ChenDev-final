package mobileMedical.Algorithm.General;




public class QR {
	private double[][] a;
	private double[] b;
	private int n;

	private int len;
	private double x[];
	private double[] py;
	
	public QR(double[][] amatrix, double[] y)
	{
		n=3;
		a = amatrix;
		py = y;
		len = y.length;

	}
	

	
	public double [] compute(){
		
		x=new double[n];
		for (int idx = 0; idx<n; idx++)
		{
			x[idx] = Double.NaN;
		}
		b=new double[len];
		for(int i=0;i<len;i++){
			b[i]=py[i];
		}
		
		double[] v=new double[len];
		double arf=0;
		double bat=0;
		double gama=0;
		for(int k=0;k<n;k++){
			arf=0;
			bat=0;
			gama=0;
			for(int i=k;i<len;i++){
				arf+=Math.pow(a[i][k],2);
			}
			arf=-(Math.abs(a[k][k])/a[k][k])*Math.sqrt(arf);
			for(int i=0;i<k;i++){
				v[i]=0;
			}
			for(int i=k;i<len;i++){
				v[i]=a[i][k];
			}
			v[k]-=arf;
			for(int i=0;i<len;i++){
				bat+=v[i]*v[i];
			}
			if(bat==0)
				continue;
			for(int j=k;j<n;j++){
				gama=0;
				for(int tem=0;tem<len;tem++){
					gama+=v[tem]*a[tem][j];
				}
				for(int tem=0;tem<len;tem++){
					a[tem][j]=a[tem][j]-(2*gama/bat)*v[tem];
				}
			}
			gama=0;
			
			for(int tem=0;tem<len;tem++){
				gama+=v[tem]*b[tem];
			}
			for(int tem=0;tem<len;tem++){
				b[tem]=b[tem]-(2*gama/bat)*v[tem];
			}
			
			
		}
		
		
		for(int j=n-1;j>=0;j--){
			if(a[j][j]==0){
            return x;
			}
			x[j]=b[j]/a[j][j];
			for(int i=0;i<j;i++){
				b[i]=b[i]-a[i][j]*x[j];
			}
			
		}

		double cancha=0;
		for(int i=n;i<len;i++)
			cancha+=Math.pow(b[i],2);

		return x;
	}
}
