import java.util.Random;
import java.util.Scanner;
import java.text.DecimalFormat;

public class fcfs
{
	// Class variables
    public static double clock, meanInterArrivalTime, meanServiceTime, sigma, totalBusy, totalWaiting, totalTurnAround, sumResponseTime, maxArival;
    public static int totalCustomers, totalCustomersBeforeSimu, queueLength, maxQueueLength, numberOfDepartures, longService;
    public static int cusIndex1,cusIndex2,i;
    public static double cusService[];
    public static double clockService[];
    public static double cusArival[];
    public static double cusInterArival[];
    public static double cusWaiting[];
    public static Random stream;
    
    public static void initialization()
    {
        clock = 0.0;
        queueLength = 0;
        totalBusy = 0;
        totalWaiting = 0.0;
    	totalTurnAround = 0.0;
        maxQueueLength = 0;
        sumResponseTime = 0;
        numberOfDepartures = 0;
        longService = 0;
        cusIndex2 = 0;        
    }

    public static double exponential(Random rng, double mean)
    {
        return -mean * Math.log(rng.nextDouble());
    }

    public static double SaveNormal;
    public static int NumNormals = 0;
    public static final double PI = 3.1415927;

    public static double normal (Random rng,double mean, double sigma)
    {
        double ReturnNormal;

        if(NumNormals==0)
        {
            double r1 = rng.nextDouble();
            double r2 = rng.nextDouble();
            ReturnNormal = Math.sqrt(-2*Math.log(r1))*Math.cos(2*PI*r2);
            SaveNormal = Math.sqrt(-2*Math.log(r1))*Math.sin(2*PI*r2);
            NumNormals = 1;
        }
        else
        {
            NumNormals = 0;
            ReturnNormal = SaveNormal;
        }
        return ReturnNormal*sigma+mean;
    }
    
    public static void main(String argv[])
    {
        boolean loop_1 = true; // loop_1 will be true when there is no customer in the queue and new arrival is needed. When new arrival is not need then loop_1 will be false.
        boolean condition1 = true; // condition1 = true, means, cusIndex1-th is in service and it is come from the queue. And condition1 = false, means cusIndex1-th is in service and it arrive directly in the service
        boolean condition2 = false; // condition2 = true, means, clockService of cudIndex1-th customer is measured previously, otherwise condition1 = false. The range of cudIndex1 is 2nd to last customer.
        boolean condition3 = false; // condition3 = false, means, last customer is not in queue. If condition3 = true, then it is considered that last customer is arrived and it is in the queue.
        meanInterArrivalTime = 4.5;
        meanServiceTime = 3.2;
        sigma = 0.6;
        totalCustomers = 25;
        DecimalFormat df = new DecimalFormat("#0.00"); // For displaying faction part with two digits
        long seed = 1232;
        //long seed = Long.parseLong(argv[0]);

        stream = new Random(seed);
        //stream = new Random(stream.nextLong()); // initialize rng stream
        initialization();
        cusArival = new double[totalCustomers+1];
        cusService = new double[totalCustomers+1];
        clockService = new double[totalCustomers+1];
        cusInterArival = new double[totalCustomers+1];
        cusWaiting = new double[totalCustomers+1];
        
        Scanner sc=new Scanner(System.in);
		System.out.println("Enter no of customers who are arrived before simulation is started: ");
		totalCustomersBeforeSimu=sc.nextInt();
        
		if(totalCustomersBeforeSimu == 0)
		{
			cusIndex1 = 1;
	        cusArival[cusIndex1] = 0.0; //simulation will be started at time 0 and customer arrival, customer at service and customer in queue before time 0 will not be considered
	        maxArival = cusArival[cusIndex1];
	        cusService[cusIndex1] = normal(stream, meanServiceTime,sigma);
	        clockService[cusIndex1] = cusService[1];
	        cusWaiting[cusIndex1] = 0.0;
	        
	        // Loop until "TotalCustomers" have departed
	        i = 2;
	        while(numberOfDepartures < totalCustomers)
	        {
	        	if(loop_1 == false) // customer already arrived, so new arrival is not needed for first loop each time
	        	{
	        		for(;i<=totalCustomers;i++)
	        		{
	        			clockService[cusIndex1] = clockService[cusIndex1-1] + cusService[cusIndex1];
	        			if(clockService[cusIndex1] > cusArival[i]) // This checks customer will go to queue or not.
	        			{
	        				queueLength++;
	        				if(maxQueueLength < queueLength)
	        				{
	        					maxQueueLength = queueLength;
	        				}
	        				
	        				if(i < totalCustomers) // If i-th customer is in queue then new arrival(i.e. (i+1)th customer) will be occured 
	        				{
	        					cusInterArival[i+1] = exponential(stream, meanInterArrivalTime);
	                			cusArival[i+1] = cusArival[i] + cusInterArival[i+1];
	                			cusService[i+1] = normal(stream, meanServiceTime,sigma);
	        				}
	        				
	        				if(i == totalCustomers) // This checks last customer is already arrived or not
	        				{
	        					condition3 = true; // condition3 = true, means, last customer is arrived and it is in the queue.
	        					break;
	        				}
	        			}
	        			else
	        			{
	        				if(queueLength >= 1 && cusIndex1 > 1)
	        				{
	        					condition2 = true; // condition2 = true, means, clockService of cudIndex1-th customer is measured previously
	        				}
	        				break;
	        			}
	        		}
	        	}
	        	
	        	if(loop_1 == true)
	        	{
	        		for(;i<=totalCustomers;i++)
	        		{
	        			cusInterArival[i] = exponential(stream, meanInterArrivalTime);
	        			cusArival[i] = cusArival[i-1] + cusInterArival[i];
	        			cusService[i] = normal(stream, meanServiceTime,sigma);
	        			if(clockService[cusIndex1] > cusArival[i])
	        			{
	        				queueLength++;
	        				if(maxQueueLength < queueLength)
	        				{
	        					maxQueueLength = queueLength;
	        				}
	        				
	        				if(i == totalCustomers)
	        				{
	        					condition1 = false; // condition1 = false, means, clockService of cusInded1-th customer has measured previously
	        					condition3 = true; // condition3 = true, means, last customer is arrived and it is in the queue.
	        					break;
	        				}
	        			}
	        			else
	        			{
	        				if(queueLength >= 1)
	        				{
	        					loop_1 = false;
	        					if(cusIndex1 > 1) 
	        					{
	        						condition2 = true; // condition2 = true, means, clockService of cudIndex1-th customer is measured previously
	        					}
	        				}
	        				break;
	        			}
	        		}	
	        	}
	        	
	        	if(queueLength == 0 && i == totalCustomers)
	        	{		
	        		cusWaiting[cusIndex1] = clockService[cusIndex1] - (cusService[cusIndex1] + cusArival[cusIndex1]);
	        		System.out.println("Customer "+cusIndex1+": Interarrival Time="+df.format(cusInterArival[cusIndex1])+", Arrival Time="+df.format(cusArival[cusIndex1])+", Service Time="+df.format(cusService[cusIndex1])+", Waiting Time="+df.format(cusWaiting[cusIndex1]));
	            	numberOfDepartures++;
	            	cusIndex1++;
	        		clockService[cusIndex1] = cusArival[cusIndex1] + cusService[cusIndex1];
	        		cusWaiting[cusIndex1] = clockService[cusIndex1] - cusService[cusIndex1] - cusArival[cusIndex1];
	        		totalWaiting = totalWaiting + cusWaiting[cusIndex1];
	        		totalTurnAround = totalTurnAround + (cusWaiting[cusIndex1] + cusService[cusIndex1]);
	        		System.out.println("Customer "+cusIndex1+": Interarrival Time="+df.format(cusInterArival[cusIndex1])+", Arrival Time="+df.format(cusArival[cusIndex1])+", Service Time="+df.format(cusService[cusIndex1])+", Waiting Time="+df.format(cusWaiting[cusIndex1]));
	            	numberOfDepartures++;
	        	}
	        	
	        	if(i == totalCustomers  && numberOfDepartures < totalCustomers && queueLength >= 1 && condition3 == true) // The last customer has arrived and it is in the queue
	        	{
	        		if(condition1 == true) // This customer is in service and it is come from the queue.
	        		{
	        			clockService[cusIndex1] = clockService[cusIndex1-1] + cusService[cusIndex1];
	        			cusWaiting[cusIndex1] = clockService[cusIndex1] - (cusService[cusIndex1] + cusArival[cusIndex1]);
	        		}
	        		
	        		if(condition1 == false) // clockService of cusInded1-th customer has measured previously
	        		{
	        			cusWaiting[cusIndex1] = clockService[cusIndex1] - (cusService[cusIndex1] + cusArival[cusIndex1]);
	        		}
	        		totalWaiting = totalWaiting + cusWaiting[cusIndex1];
	        		totalTurnAround = totalTurnAround + (cusWaiting[cusIndex1] + cusService[cusIndex1]);
	        		System.out.println("Customer "+cusIndex1+": Interarrival Time="+df.format(cusInterArival[cusIndex1])+", Arrival Time="+df.format(cusArival[cusIndex1])+", Service Time="+df.format(cusService[cusIndex1])+", Waiting Time="+df.format(cusWaiting[cusIndex1]));
	            	numberOfDepartures++;
	            	cusIndex1++;
	            	if(queueLength == 1) // this is for last customer who has waited in the queue
	            	{
	            		clockService[cusIndex1] = clockService[cusIndex1-1] + cusService[cusIndex1];
	                	cusWaiting[cusIndex1] = clockService[cusIndex1] - (cusService[cusIndex1] + cusArival[cusIndex1]);
	                	totalWaiting = totalWaiting + cusWaiting[cusIndex1];
	                	totalTurnAround = totalTurnAround + (cusWaiting[cusIndex1] + cusService[cusIndex1]);
	                	System.out.println("Customer "+cusIndex1+": Interarrival Time="+df.format(cusInterArival[cusIndex1])+", Arrival Time="+df.format(cusArival[cusIndex1])+", Service Time="+df.format(cusService[cusIndex1])+", Waiting Time="+df.format(cusWaiting[cusIndex1]));
	                	queueLength--;
	                	numberOfDepartures++;
	            	}
	            	
	        		if(queueLength > 1) // this is for up to last customer who have waited in the queue
	        		{
	        			int p,q;
		        		p = cusIndex1;
		        		q = queueLength;
		        		
		        		for(; cusIndex1 <= (p + q)-1; cusIndex1++)
		        		{
		                	clockService[cusIndex1] = clockService[cusIndex1-1] + cusService[cusIndex1];
		                	cusWaiting[cusIndex1] = clockService[cusIndex1] - (cusService[cusIndex1] + cusArival[cusIndex1]);
		                	totalWaiting = totalWaiting + cusWaiting[cusIndex1];
		                	totalTurnAround = totalTurnAround + (cusWaiting[cusIndex1] + cusService[cusIndex1]);
		                	System.out.println("Customer "+cusIndex1+": Interarrival Time="+df.format(cusInterArival[cusIndex1])+", Arrival Time="+df.format(cusArival[cusIndex1])+", Service Time="+df.format(cusService[cusIndex1])+", Waiting Time="+df.format(cusWaiting[cusIndex1]));
		                	numberOfDepartures++;
		                	queueLength--;
		                	if(cusIndex1 == totalCustomers)
		                	{
		                		break;
		                	}
		        		}
	        		}
	        	}
	        	
	        	if(i == totalCustomers  && numberOfDepartures < totalCustomers && queueLength >= 1 && condition3 == false) // condition3 = false, means, last customer is arrived, but not in queue.
	        	{
	        		if(condition1 == true) // This customer is in service and it is come from the queue.
	        		{
	        			clockService[cusIndex1] = clockService[cusIndex1-1] + cusService[cusIndex1];
	        			cusWaiting[cusIndex1] = clockService[cusIndex1] - (cusService[cusIndex1] + cusArival[cusIndex1]);
	        		}
	        		
	        		if(condition1 == false) // clockService of cusInded1-th customer has measured previously
	        		{
	        			cusWaiting[cusIndex1] = clockService[cusIndex1] - (cusService[cusIndex1] + cusArival[cusIndex1]);
	        		}
	        		totalWaiting = totalWaiting + cusWaiting[cusIndex1];
	        		totalTurnAround = totalTurnAround + (cusWaiting[cusIndex1] + cusService[cusIndex1]);
	        		System.out.println("Customer "+cusIndex1+": Interarrival Time="+df.format(cusInterArival[cusIndex1])+", Arrival Time="+df.format(cusArival[cusIndex1])+", Service Time="+df.format(cusService[cusIndex1])+", Waiting Time="+df.format(cusWaiting[cusIndex1]));
	        		numberOfDepartures++;
	        		queueLength--;
	            	cusIndex1++;
	        	}
	        	
	        	if(numberOfDepartures < totalCustomers && i < totalCustomers)
	        	{
	            	if(queueLength == 0)
	            	{		
	            		loop_1 = true; // Now new arrival is needed
	            		cusWaiting[cusIndex1] = clockService[cusIndex1] - (cusService[cusIndex1] + cusArival[cusIndex1]);
	            		totalWaiting = totalWaiting + cusWaiting[cusIndex1];
	            		totalTurnAround = totalTurnAround + (cusWaiting[cusIndex1] + cusService[cusIndex1]);
	            		System.out.println("Customer "+cusIndex1+": Interarrival Time="+df.format(cusInterArival[cusIndex1])+", Arrival Time="+df.format(cusArival[cusIndex1])+", Service Time="+df.format(cusService[cusIndex1])+", Waiting Time="+df.format(cusWaiting[cusIndex1]));
	            		i++;
	            		cusIndex1++;
	            		clockService[cusIndex1] = cusArival[cusIndex1] + cusService[cusIndex1];
	            	}
	            	
	            	if(queueLength >= 1 && cusIndex1 == 1) // This is only for 1st customer. For 1st customer the cusIndex1 = 1
	            	{
	            		clockService[cusIndex1] = cusService[cusIndex1];
	            		cusWaiting[cusIndex1] = 0.0;
	            		totalWaiting = totalWaiting + cusWaiting[cusIndex1];
	            		totalTurnAround = totalTurnAround + (cusWaiting[cusIndex1] + cusService[cusIndex1]);
	            		System.out.println("Customer "+cusIndex1+": Interarrival Time="+df.format(cusInterArival[cusIndex1])+", Arrival Time="+df.format(cusArival[cusIndex1])+", Service Time="+df.format(cusService[cusIndex1])+", Waiting Time="+df.format(cusWaiting[cusIndex1]));
	            		queueLength--;
	            		cusIndex1++;
	            	}
	        	
	            	if(queueLength >= 1 && cusIndex1 > 1 && condition2 == true) // condition2 = true, means, clockService of cudIndex1-th customer is measured previously
	            	{
	            		cusWaiting[cusIndex1] = clockService[cusIndex1] - (cusService[cusIndex1] + cusArival[cusIndex1]);
	            		totalWaiting = totalWaiting + cusWaiting[cusIndex1];
	            		totalTurnAround = totalTurnAround + (cusWaiting[cusIndex1] + cusService[cusIndex1]);
	            		System.out.println("Customer "+cusIndex1+": Interarrival Time="+df.format(cusInterArival[cusIndex1])+", Arrival Time="+df.format(cusArival[cusIndex1])+", Service Time="+df.format(cusService[cusIndex1])+", Waiting Time="+df.format(cusWaiting[cusIndex1]));
	            		queueLength--;
	            		cusIndex1++;
	            		clockService[cusIndex1] = clockService[cusIndex1-1] + cusService[cusIndex1];
	            	}
	            	numberOfDepartures++;
	        	}
	        }
		}
		else
		{
			for(i = 1; i <= totalCustomersBeforeSimu; i++)
			{
				cusArival[i] = 0.0;
				cusService[i] = normal(stream, meanServiceTime,sigma);
				queueLength++;				
			}
			
			cusIndex1 = 1;
	        clockService[cusIndex1] = cusService[cusIndex1];
	        queueLength--;
	        
	        while(numberOfDepartures < totalCustomers)
	        {
	        	if(loop_1 == false) // Customer has already arrived, so each time, for the first iteration (i.e. the first loop) new arrival is not needed
	        	{
	        		for(;i<=totalCustomers;i++)
	        		{
	        			clockService[cusIndex1] = clockService[cusIndex1-1] + cusService[cusIndex1];
	        			if(clockService[cusIndex1] > cusArival[i]) // This checks that customer will go to in the queue or not.
	        			{
	        				queueLength++;
	        				if(maxQueueLength < queueLength)
	        				{
	        					maxQueueLength = queueLength;
	        				}
	        				
	        				if(i < totalCustomers) // If i-th customer is in queue then new arrival(i.e. (i+1)-th customer) will be occur 
	        				{
	        					cusInterArival[i+1] = exponential(stream, meanInterArrivalTime);
	                			cusArival[i+1] = cusArival[i] + cusInterArival[i+1];
	                			cusService[i+1] = normal(stream, meanServiceTime,sigma);
	        				}
	        				
	        				if(i == totalCustomers) // This checks last customer is already arrived or not
	        				{
	        					condition3 = true; // condition3 = true, means, last customer is arrived and it is in the queue.
	        					break;
	        				}
	        			}
	        			else
	        			{
	        				if(queueLength >= 1 && cusIndex1 > 1)
	        				{
	        					condition2 = true; // condition2 = true, means, clockService of cusIndex1-th customer is measured previously
	        				}
	        				break;
	        			}
	        		}
	        	}
	        	
	        	if(loop_1 == true)
	        	{
	        		for(;i<=totalCustomers;i++)
	        		{
	        			cusInterArival[i] = exponential(stream, meanInterArrivalTime);
	        			cusArival[i] = cusArival[i-1] + cusInterArival[i];
	        			cusService[i] = normal(stream, meanServiceTime,sigma);
	        			if(clockService[cusIndex1] > cusArival[i]) // This checks that customer will go to in the queue or not
	        			{
	        				queueLength++;
	        				if(maxQueueLength < queueLength)
	        				{
	        					maxQueueLength = queueLength;
	        				}
	        				
	        				if(i == totalCustomers)
	        				{
	        					condition1 = false; // condition1 = false, means, clockService of cusInded1-th customer has measured previously
	        					condition3 = true; // condition3 = true, means, last customer is arrived and it is in the queue.
	        					break;
	        				}
	        			}
	        			else
	        			{
	        				if(queueLength >= 1)
	        				{
	        					loop_1 = false;
	        					if(cusIndex1 > 1) 
	        					{
	        						condition2 = true; // condition2 = true, means, clockService of cudIndex1-th customer is measured previously
	        					}
	        				}
	        				break;
	        			}
	        		}	
	        	}
	        	
	        	if(queueLength == 0 && i == totalCustomers)
	        	{		
	        		cusWaiting[cusIndex1] = clockService[cusIndex1] - (cusService[cusIndex1] + cusArival[cusIndex1]);
	        		System.out.println("Customer "+cusIndex1+": Interarrival Time="+df.format(cusInterArival[cusIndex1])+", Arrival Time="+df.format(cusArival[cusIndex1])+", Service Time="+df.format(cusService[cusIndex1])+", Waiting Time="+df.format(cusWaiting[cusIndex1]));
	            	numberOfDepartures++;
	            	cusIndex1++;
	        		clockService[cusIndex1] = cusArival[cusIndex1] + cusService[cusIndex1];
	        		cusWaiting[cusIndex1] = clockService[cusIndex1] - cusService[cusIndex1] - cusArival[cusIndex1];
	        		totalWaiting = totalWaiting + cusWaiting[cusIndex1];
	        		totalTurnAround = totalTurnAround + (cusWaiting[cusIndex1] + cusService[cusIndex1]);
	        		System.out.println("Customer "+cusIndex1+": Interarrival Time="+df.format(cusInterArival[cusIndex1])+", Arrival Time="+df.format(cusArival[cusIndex1])+", Service Time="+df.format(cusService[cusIndex1])+", Waiting Time="+df.format(cusWaiting[cusIndex1]));
	            	numberOfDepartures++;
	        	}
	        	
	        	if(i == totalCustomers  && numberOfDepartures < totalCustomers && queueLength >= 1 && condition3 == true) // The last customer has arrived and it is in the queue
	        	{
	        		if(condition1 == true) // This customer is in service and it is come from the queue.
	        		{
	        			clockService[cusIndex1] = clockService[cusIndex1-1] + cusService[cusIndex1];
	        			cusWaiting[cusIndex1] = clockService[cusIndex1] - (cusService[cusIndex1] + cusArival[cusIndex1]);
	        		}
	        		
	        		if(condition1 == false) // clockService of cusInded1-th customer has measured previously
	        		{
	        			cusWaiting[cusIndex1] = clockService[cusIndex1] - (cusService[cusIndex1] + cusArival[cusIndex1]);
	        		}
	        		totalWaiting = totalWaiting + cusWaiting[cusIndex1];
	        		totalTurnAround = totalTurnAround + (cusWaiting[cusIndex1] + cusService[cusIndex1]);
	        		System.out.println("Customer "+cusIndex1+": Interarrival Time="+df.format(cusInterArival[cusIndex1])+", Arrival Time="+df.format(cusArival[cusIndex1])+", Service Time="+df.format(cusService[cusIndex1])+", Waiting Time="+df.format(cusWaiting[cusIndex1]));
	            	numberOfDepartures++;
	            	cusIndex1++;
	            	if(queueLength == 1) // this is for last customer who has waited in the queue
	            	{
	            		clockService[cusIndex1] = clockService[cusIndex1-1] + cusService[cusIndex1];
	                	cusWaiting[cusIndex1] = clockService[cusIndex1] - (cusService[cusIndex1] + cusArival[cusIndex1]);
	                	totalWaiting = totalWaiting + cusWaiting[cusIndex1];
	                	totalTurnAround = totalTurnAround + (cusWaiting[cusIndex1] + cusService[cusIndex1]);
	                	System.out.println("Customer "+cusIndex1+": Interarrival Time="+df.format(cusInterArival[cusIndex1])+", Arrival Time="+df.format(cusArival[cusIndex1])+", Service Time="+df.format(cusService[cusIndex1])+", Waiting Time="+df.format(cusWaiting[cusIndex1]));
	                	queueLength--;
	                	numberOfDepartures++;
	            	}
	            	
	        		if(queueLength > 1) // this is for up to last customer who have waited in the queue
	        		{
	        			int p,q;
		        		p = cusIndex1;
		        		q = queueLength;
		        		
		        		for(; cusIndex1 <= (p + q)-1; cusIndex1++)
		        		{
		                	clockService[cusIndex1] = clockService[cusIndex1-1] + cusService[cusIndex1];
		                	cusWaiting[cusIndex1] = clockService[cusIndex1] - (cusService[cusIndex1] + cusArival[cusIndex1]);
		                	totalWaiting = totalWaiting + cusWaiting[cusIndex1];
		                	totalTurnAround = totalTurnAround + (cusWaiting[cusIndex1] + cusService[cusIndex1]);
		                	System.out.println("Customer "+cusIndex1+": Interarrival Time="+df.format(cusInterArival[cusIndex1])+", Arrival Time="+df.format(cusArival[cusIndex1])+", Service Time="+df.format(cusService[cusIndex1])+", Waiting Time="+df.format(cusWaiting[cusIndex1]));
		                	numberOfDepartures++;
		                	queueLength--;
		                	if(cusIndex1 == totalCustomers)
		                	{
		                		break;
		                	}
		        		}
	        		}
	        	}
	        	
	        	if(i == totalCustomers  && numberOfDepartures < totalCustomers && queueLength >= 1 && condition3 == false) // condition3 = false, means, last customer is arrived, but not in queue.
	        	{
	        		if(condition1 == true) // This customer is in service and it is come from the queue.
	        		{
	        			clockService[cusIndex1] = clockService[cusIndex1-1] + cusService[cusIndex1];
	        			cusWaiting[cusIndex1] = clockService[cusIndex1] - (cusService[cusIndex1] + cusArival[cusIndex1]);
	        		}
	        		
	        		if(condition1 == false) // clockService of cusInded1-th customer has measured previously
	        		{
	        			cusWaiting[cusIndex1] = clockService[cusIndex1] - (cusService[cusIndex1] + cusArival[cusIndex1]);
	        		}
	        		totalWaiting = totalWaiting + cusWaiting[cusIndex1];
	        		totalTurnAround = totalTurnAround + (cusWaiting[cusIndex1] + cusService[cusIndex1]);
	        		System.out.println("Customer "+cusIndex1+": Interarrival Time="+df.format(cusInterArival[cusIndex1])+", Arrival Time="+df.format(cusArival[cusIndex1])+", Service Time="+df.format(cusService[cusIndex1])+", Waiting Time="+df.format(cusWaiting[cusIndex1]));
	        		numberOfDepartures++;
	        		queueLength--;
	            	cusIndex1++;
	        	}
	        	
	        	if(numberOfDepartures < totalCustomers && i < totalCustomers)
	        	{
	            	if(queueLength == 0)
	            	{		
	            		loop_1 = true; // Now new arrival is needed
	            		cusWaiting[cusIndex1] = clockService[cusIndex1] - (cusService[cusIndex1] + cusArival[cusIndex1]);
	            		totalWaiting = totalWaiting + cusWaiting[cusIndex1];
	            		totalTurnAround = totalTurnAround + (cusWaiting[cusIndex1] + cusService[cusIndex1]);
	            		System.out.println("Customer "+cusIndex1+": Interarrival Time="+df.format(cusInterArival[cusIndex1])+", Arrival Time="+df.format(cusArival[cusIndex1])+", Service Time="+df.format(cusService[cusIndex1])+", Waiting Time="+df.format(cusWaiting[cusIndex1]));
	            		i++;
	            		cusIndex1++;
	            		clockService[cusIndex1] = cusArival[cusIndex1] + cusService[cusIndex1];
	            	}
	            	
	            	if(queueLength >= 1 && cusIndex1 == 1) // This is only for 1st customer. For 1st customer the cusIndex1 = 1
	            	{
	            		clockService[cusIndex1] = cusService[cusIndex1];
	            		cusWaiting[cusIndex1] = 0.0;
	            		totalWaiting = totalWaiting + cusWaiting[cusIndex1];
	            		totalTurnAround = totalTurnAround + (cusWaiting[cusIndex1] + cusService[cusIndex1]);
	            		System.out.println("Customer "+cusIndex1+": Interarrival Time="+df.format(cusInterArival[cusIndex1])+", Arrival Time="+df.format(cusArival[cusIndex1])+", Service Time="+df.format(cusService[cusIndex1])+", Waiting Time="+df.format(cusWaiting[cusIndex1]));
	            		queueLength--;
	            		cusIndex1++;
	            	}
	        	
	            	if(queueLength >= 1 && cusIndex1 > 1 && condition2 == true) // condition2 = true, means, clockService of cudIndex1-th customer is measured previously
	            	{
	            		cusWaiting[cusIndex1] = clockService[cusIndex1] - (cusService[cusIndex1] + cusArival[cusIndex1]);
	            		totalWaiting = totalWaiting + cusWaiting[cusIndex1];
	            		totalTurnAround = totalTurnAround + (cusWaiting[cusIndex1] + cusService[cusIndex1]);
	            		System.out.println("Customer "+cusIndex1+": Interarrival Time="+df.format(cusInterArival[cusIndex1])+", Arrival Time="+df.format(cusArival[cusIndex1])+", Service Time="+df.format(cusService[cusIndex1])+", Waiting Time="+df.format(cusWaiting[cusIndex1]));
	            		queueLength--;
	            		cusIndex1++;
	            		clockService[cusIndex1] = clockService[cusIndex1-1] + cusService[cusIndex1];
	            	}
	            	numberOfDepartures++;
	        	}
	        }
		}		
   
    	System.out.println("Average Waiting Time = "+df.format(totalWaiting/totalCustomers));
    	System.out.println("Average Turnaround Time = "+df.format(totalTurnAround/totalCustomers));
        System.out.println("maxQueueLength = "+maxQueueLength);
        System.out.println("Total simulation time = "+df.format(clockService[totalCustomers]));
    }
}
