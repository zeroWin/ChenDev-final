package mobileMedical.Algorithm.EMD;

import java.math.MathContext;

import com.artfulbits.aiCharts.Base.MathUtils;




public class EMD{
	
/*private float threshold;
private float tolerance;
private float maxOmfs;*/
private final int NBSYM = 2;
private final int MAX_ITERATIONS = 1000;


public ImfList Process(double[] xData,double[] yData, int maxImfs, double threshold, double tolerance)
{
   
	
    /* declarations */
	  int i,n,nb_imfs,max_imfs,iteration_counter,stop_status,stop_EMD;
	  Extrema ex;
	  Input input;
	  Envelop env;
	  Stop stop_params;
	  double[] x, y,z,m,a;
	  ImfList list;
	  input = new Input();
	  
	  input.y = yData;
	  input.n = yData.length;
	  input.x = xData;
	  
	 /* for (int index = 0; index< input.n; index++)
	  {
		  input.x[index] = index;
	  }
	  */
	  input.max_imfs = maxImfs; 
	  input.stop_params.threshold = threshold;
	  input.stop_params.tolerance = tolerance;
	    /* get input data */


	  n=input.n;
	  max_imfs=input.max_imfs;
	  stop_params=input.stop_params;
	  x=input.x;
	  y=input.y;
	  
	    /* initialisations */
	  ex=init_extr(n+2*NBSYM);
	  list=init_imf_list(n);
	  z= new double[n];
	  m= new double[n];;
	  a= new double[n];;
	  env=init_local_mean(n+2*NBSYM);
	  
	  
	    /* MAIN LOOP */
	  
	  nb_imfs=0;
	  stop_EMD=0;
	  
	  while ((max_imfs == 0 || (nb_imfs < max_imfs)) && stop_EMD == 0) {
	    
	        /* initialisation */
	    for (i=0;i<n;i++) z[i]=y[i];
	    for (i=0;i<n;i++) m[i]=y[i];
	    iteration_counter=0;
	    
	    stop_status = mean_and_amplitude(x,z,m,a,n,ex,env);
	    
	        /* SIFTING LOOP */
	    
	    while (stop_status == 0 && stop_sifting(m,a,ex,stop_params,n,iteration_counter) == 0) {
	      
	            /* subtract the local mean */
	      for (i=0;i<n;i++) z[i]=z[i]-m[i];
	      iteration_counter++;
	      
	      stop_status = mean_and_amplitude(x,z,m,a,n,ex,env);
	      
	      
	    }
	    
	        /* save current IMF into list if at least     */
	        /* one sifting iteration has been performed */
	    if (iteration_counter !=0 ) {
	      add_imf(list,z,iteration_counter);
	      nb_imfs++;
	      for (i=0;i<n;i++) y[i]=y[i]-z[i];
	      
	    }
	    else
	      stop_EMD = 1;
	    
	  }
	  
	    /* save the residual into list */
	  add_imf(list,y,0);
	  
	  return list;
	}

private int mean_and_amplitude(double[] x, double[] z, double[] m, double[] a, int n,Extrema ex, Envelop env)
{
	  int i;
	  /* detect maxima and minima */
	  extr(x,z,n,ex);
	  /* if not enough extrema . stop */
	  if (ex.n_min+ex.n_max <7)
	    return 1;
	  /* add extra points at the edges */
	  boundary_conditions(x,z,n,ex);
	  /* interpolation - upper envelope */
	  interpolation(env.e_max,ex.x_max,ex.y_max,ex.n_max,x,n,env.tmp1,env.tmp2);
	  /* interpolation - lower envelope */
	  interpolation(env.e_min,ex.x_min,ex.y_min,ex.n_min,x,n,env.tmp1,env.tmp2);
	  /* compute the mean */
	  for (i=0;i<n;i++) m[i]=(env.e_max[i]+env.e_min[i])/2;
	  /* compute the amplitude */
	  for (i=0;i<n;i++) a[i]=(env.e_max[i]-env.e_min[i])/2;
	  return 0;


}

private void extr(double[] x,double[] y,int n,Extrema ex)
{

	 int cour;
	    ex.n_min=0;
	    ex.n_max=0;
	    
	  /* search for extrema */
	    for(cour=1;cour<(n-1);cour++) {
	        if (y[cour]<=y[cour-1] && y[cour]<=y[cour+1]) /* local minimum */ {
	            ex.x_min[ex.n_min+NBSYM]=x[cour];
	            ex.y_min[ex.n_min+NBSYM]=y[cour];
	            ex.n_min++;
	        }
	        if (y[cour]>=y[cour-1] && y[cour]>=y[cour+1]) /* local maximum */ {
	            ex.x_max[ex.n_max+NBSYM]=x[cour];
	            ex.y_max[ex.n_max+NBSYM]=y[cour];
	            ex.n_max++;
	        }
	    }
}


private void boundary_conditions(double[] x,double[] y,int n,Extrema ex) 
{
    int cour;
    int nbsymt;
    
     nbsymt = NBSYM;
    /* reduce the number of symmetrized points if there is not enough extrema */
    while(ex.n_min < nbsymt+1 && ex.n_max < nbsymt+1) nbsymt--;
    if (nbsymt < NBSYM) {
        for(cour=0;cour<ex.n_max;cour++) {
            ex.x_max[nbsymt+cour] = ex.x_max[NBSYM+cour];
            ex.y_max[nbsymt+cour] = ex.y_max[NBSYM+cour];
        }
        for(cour=0;cour<ex.n_min;cour++) {
            ex.x_min[nbsymt+cour] = ex.x_min[NBSYM+cour];
            ex.y_min[nbsymt+cour] = ex.y_min[NBSYM+cour];
        }
    }
    
    /* select the symmetrized points and the axis of symmetry at the beginning of the signal*/
    if (ex.x_max[nbsymt] < ex.x_min[nbsymt]) { /* first = max */
        if (y[0] > ex.y_min[nbsymt]) { /* the edge is not a min */
            if (2*ex.x_max[nbsymt]-ex.x_min[2*nbsymt-1] > x[0]) { /* symmetrized parts are too short */
                for(cour=0;cour<nbsymt;cour++) {
                    ex.x_max[cour] = 2*x[0]-ex.x_max[2*nbsymt-1-cour];
                    ex.y_max[cour] = ex.y_max[2*nbsymt-1-cour];
                    ex.x_min[cour] = 2*x[0]-ex.x_min[2*nbsymt-1-cour];
                    ex.y_min[cour] = ex.y_min[2*nbsymt-1-cour];
                }
            } else { /* symmetrized parts are long enough */
                for(cour=0;cour<nbsymt;cour++) {
                    ex.x_max[cour] = 2*ex.x_max[nbsymt]-ex.x_max[2*nbsymt-cour];
                    ex.y_max[cour] = ex.y_max[2*nbsymt-cour];
                    ex.x_min[cour] = 2*ex.x_max[nbsymt]-ex.x_min[2*nbsymt-1-cour];
                    ex.y_min[cour] = ex.y_min[2*nbsymt-1-cour];
                }
            }
        } else { /* edge is a min . sym with respect to the edge*/
            for(cour=0;cour<nbsymt;cour++) {
                ex.x_max[cour] = 2*x[0]-ex.x_max[2*nbsymt-1-cour];
                ex.y_max[cour] = ex.y_max[2*nbsymt-1-cour];
            }
            for(cour=0;cour<nbsymt-1;cour++) {
                ex.x_min[cour] = 2*x[0]-ex.x_min[2*nbsymt-2-cour];
                ex.y_min[cour] = ex.y_min[2*nbsymt-2-cour];
            }
            ex.x_min[nbsymt-1] = x[0];
            ex.y_min[nbsymt-1] = y[0];
        }
    } else { /* first = min */
        
        if (y[0] < ex.y_max[nbsymt]) { /* the edge is not a max */
            if (2*ex.x_min[nbsymt]-ex.x_max[2*nbsymt-1] > x[0]) { /* symmetrized parts are too short */
                for(cour=0;cour<nbsymt;cour++) {
                    ex.x_max[cour] = 2*x[0]-ex.x_max[2*nbsymt-1-cour];
                    ex.y_max[cour] = ex.y_max[2*nbsymt-1-cour];
                    ex.x_min[cour] = 2*x[0]-ex.x_min[2*nbsymt-1-cour];
                    ex.y_min[cour] = ex.y_min[2*nbsymt-1-cour];
                }
            } else { /* symmetrized parts are long enough */
                for(cour=0;cour<nbsymt;cour++) {
                    ex.x_max[cour] = 2*ex.x_min[nbsymt]-ex.x_max[2*nbsymt-1-cour];
                    ex.y_max[cour] = ex.y_max[2*nbsymt-1-cour];
                    ex.x_min[cour] = 2*ex.x_min[nbsymt]-ex.x_min[2*nbsymt-cour];
                    ex.y_min[cour] = ex.y_min[2*nbsymt-cour];
                }
            }
        } else { /* edge is a max . sym with respect to the edge*/
            for(cour=0;cour<nbsymt;cour++) {
                ex.x_min[cour] = 2*x[0]-ex.x_min[2*nbsymt-1-cour];
                ex.y_min[cour] = ex.y_min[2*nbsymt-1-cour];
            }
            for(cour=0;cour<nbsymt-1;cour++) {
                ex.x_max[cour] = 2*x[0]-ex.x_max[2*nbsymt-2-cour];
                ex.y_max[cour] = ex.y_max[2*nbsymt-2-cour];
            }
            ex.x_max[nbsymt-1] = x[0];
            ex.y_max[nbsymt-1] = y[0];
        }
    }
    
    
    (ex.n_min) += nbsymt-1;
    (ex.n_max) += nbsymt-1;
    
    /* select the symmetrized points and the axis of symmetry at the end of the signal*/
    if (ex.x_max[ex.n_max] < ex.x_min[ex.n_min]) { /* last is a min */
        if (y[n-1] < ex.y_max[ex.n_max]) { /* the edge is not a max */
            if (2*ex.x_min[ex.n_min]-ex.x_max[ex.n_max-nbsymt+1] < x[n-1]) { /* symmetrized parts are too short */
                for(cour=0;cour<nbsymt;cour++) {
                    ex.x_max[ex.n_max+1+cour] = 2*x[n-1]-ex.x_max[ex.n_max-cour];
                    ex.y_max[ex.n_max+1+cour] = ex.y_max[ex.n_max-cour];
                    ex.x_min[ex.n_min+1+cour] = 2*x[n-1]-ex.x_min[ex.n_min-cour];
                    ex.y_min[ex.n_min+1+cour] = ex.y_min[ex.n_min-cour];
                }
            } else { /* symmetrized parts are long enough */
                for(cour=0;cour<nbsymt;cour++) {
                    ex.x_max[ex.n_max+1+cour] = 2*ex.x_min[ex.n_min]-ex.x_max[ex.n_max-cour];
                    ex.y_max[ex.n_max+1+cour] = ex.y_max[ex.n_max-cour];
                    ex.x_min[ex.n_min+1+cour] = 2*ex.x_min[ex.n_min]-ex.x_min[ex.n_min-1-cour];
                    ex.y_min[ex.n_min+1+cour] = ex.y_min[ex.n_min-1-cour];
                }
            }
        } else { /* edge is a max . sym with respect to the edge*/
            for(cour=0;cour<nbsymt;cour++) {
                ex.x_min[ex.n_min+1+cour] = 2*x[n-1]-ex.x_min[ex.n_min-cour];
                ex.y_min[ex.n_min+1+cour] = ex.y_min[ex.n_min-cour];
            }
            for(cour=0;cour<nbsymt-1;cour++) {
                ex.x_max[ex.n_max+2+cour] = 2*x[n-1]-ex.x_max[ex.n_max-cour];
                ex.y_max[ex.n_max+2+cour] = ex.y_max[ex.n_max-cour];
            }
            ex.x_max[ex.n_max+1] = x[n-1];
            ex.y_max[ex.n_max+1] = y[n-1];
        }
    } else {  /* last is a max */
        if (y[n-1] > ex.y_min[ex.n_min]) { /* the edge is not a min */
            if (2*ex.x_max[ex.n_max]-ex.x_min[ex.n_min-nbsymt+1] < x[n-1]) { /* symmetrized parts are too short */
                for(cour=0;cour<nbsymt;cour++) {
                    ex.x_max[ex.n_max+1+cour] = 2*x[n-1]-ex.x_max[ex.n_max-cour];
                    ex.y_max[ex.n_max+1+cour] = ex.y_max[ex.n_max-cour];
                    ex.x_min[ex.n_min+1+cour] = 2*x[n-1]-ex.x_min[ex.n_min-cour];
                    ex.y_min[ex.n_min+1+cour] = ex.y_min[ex.n_min-cour];
                }
            } else { /* symmetrized parts are long enough */
                for(cour=0;cour<nbsymt;cour++) {
                    ex.x_max[ex.n_max+1+cour] = 2*ex.x_max[ex.n_max]-ex.x_max[ex.n_max-1-cour];
                    ex.y_max[ex.n_max+1+cour] = ex.y_max[ex.n_max-1-cour];
                    ex.x_min[ex.n_min+1+cour] = 2*ex.x_max[ex.n_max]-ex.x_min[ex.n_min-cour];
                    ex.y_min[ex.n_min+1+cour] = ex.y_min[ex.n_min-cour];
                }
            }
        } else { /* edge is a min . sym with respect to the edge*/
            for(cour=0;cour<nbsymt;cour++) {
                ex.x_max[ex.n_max+1+cour] = 2*x[n-1]-ex.x_max[ex.n_max-cour];
                ex.y_max[ex.n_max+1+cour] = ex.y_max[ex.n_max-cour];
            }
            for(cour=0;cour<nbsymt-1;cour++) {
                ex.x_min[ex.n_min+2+cour] = 2*x[n-1]-ex.x_min[ex.n_min-cour];
                ex.y_min[ex.n_min+2+cour] = ex.y_min[ex.n_min-cour];
            }
            ex.x_min[ex.n_min+1] = x[n-1];
            ex.y_min[ex.n_min+1] = y[n-1];
        }
    }
    
    (ex.n_min) = ex.n_min + nbsymt + 1;
    (ex.n_max) = ex.n_max + nbsymt + 1;
}

private void interpolation(double[] y,double[] xs,double[] ys,int n,double[] x, int nx,double[] ys2, double[] temp) 
{
	  int i,j,jfin,cur,prev;
	  double p,sig,a,b,c,d,e,f,g,a0,a1,a2,a3,xc;

	  /* Compute second derivatives at the knots */
	  ys2[0]=temp[0]=0.0f;
	  for (i=1;i<n-1;i++) {
	    sig=(xs[i]-xs[i-1])/(xs[i+1]-xs[i-1]);
	    p=sig*ys2[i-1]+2.0f;
	    ys2[i]=(sig-1.0f)/p;
	    temp[i]=(ys[i+1]-ys[i])/(xs[i+1]-xs[i])-(ys[i]-ys[i-1])/(xs[i]-xs[i-1]);
	    temp[i]=(6.0f*temp[i]/(xs[i+1]-xs[i-1])-sig*temp[i-1])/p;
	  }
	  ys2[n-1]=0.0f;
	  for (j=n-2;j>=0;j--) ys2[j]=ys2[j]*ys2[j+1]+temp[j];

	  /* Compute the spline coefficients */
	  cur=0;
	  j=0;
	  jfin=n-1;
	  while (xs[j+2]<x[0]) j++;
	  while (xs[jfin]>x[nx-1]) jfin--;
	  for (;j<=jfin;j++) {
	    /* Compute the coefficients of the polynomial between two knots */
	    a=xs[j];
	    b=xs[j+1];
	    c=b-a;
	    d=ys[j];
	    e=ys[j+1];
	    f=ys2[j];
	    g=ys2[j+1];
	    a0=(b*d-a*e+CUBE(b)*f/6-CUBE(a)*g/6)/c+c*(a*g-b*f)/6;
	    a1=(e-d-SQUARE(b)*f/2+SQUARE(a)*g/2)/c+c*(f-g)/6;
	    a2=(b*f-a*g)/(2*c);
	    a3=(g-f)/(6*c);


	    prev=cur;
	    while ((cur<nx) && ((j==jfin) || (x[cur]<xs[j+1]))) cur++;

	    /* Compute the value of the spline at the sampling times x[i] */
	    for (i=prev;i<cur;i++) {
	      xc=x[i];
	      y[i]=a0+a1*xc+a2*SQUARE(xc)+a3*CUBE(xc);
	    }
	  }
	}




private double SQUARE(double A) 
{
	return (A*A);
}

private  double CUBE(double A) 
{
	return (A*A*A);
}


private int stop_sifting(double[] m, double[]a,Extrema ex,Stop sp,int n, int counter) {
	  int i,count;
	  double tol,eps;
	  tol = sp.tolerance*n;
	  eps = sp.threshold;
	  count = 0;
	  if (counter >= MAX_ITERATIONS) return 1;
	  for (i=0;i<ex.n_min;i++) if (ex.y_min[i] > 0) return 0;
	  for (i=0;i<ex.n_max;i++) if (ex.y_max[i] < 0) return 0;
	  for (i=0;i<n;i++) {
	    if (emd_fabs(m[i]) > eps* emd_fabs(a[i])) if (++count>tol) return 0;
	  }
	  return 1;
	}


private double emd_fabs(double x) {
	  if (x <0) return -x;
	  else return x;
	}


private void add_imf(ImfList list,double[] p,int nb_it) {
	  double[] v = new double[list.n];
	  int i;
	  Imf mode= new Imf();
	  for (i=0;i<list.n;i++) v[i]=p[i];
	  mode.pointer=v;
	  mode.nb_iterations=nb_it;
	  mode.next= null;
	  if (list.first == null) {
	    list.first=mode;
	  } else {
	    (list.last).next=mode;
	  }
	  list.last=mode;
	  list.m++;
	}


private Extrema init_extr(int n) {
	Extrema ex = new Extrema();
    ex.x_min=  new double[n];
    ex.x_max= new double[n];
    ex.y_min= new double[n];
    ex.y_max= new double[n];
    return ex;
}

private ImfList init_imf_list(int n) {
	ImfList list = new ImfList();
	  list.first= null;
	  list.last=null;
	  list.n=n;
	  list.m=0;
	  return list;
	}


private Envelop init_local_mean(int n) {
	Envelop env = new Envelop();
	  env.e_min = new double[n];
	  env.e_max = new double[n];
	  env.tmp1 = new double[n];
	  env.tmp2 = new double[n];
	  return env;
	}
}
