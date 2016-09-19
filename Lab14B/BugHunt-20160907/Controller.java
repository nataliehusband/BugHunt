import javax.swing.JFrame; // for JFrame
import javax.swing.JOptionPane;
import java.awt.*; // for graphics & MouseListener 
import java.awt.event.*; // need for events and MouseListener
import java.util.TimerTask; // need for Timer and TimerTask classes

////////////////////////////////////
/////////////////////////////////////
///////// controller class 
////////////////////////////////////
////////////////////////////////////

class Controller extends TimerTask implements MouseListener  
{
    public static final int SLOW_BUG = 0; // these are in order
    public static final int FAST_BUG = 1;
    public static final int VERY_FAST_BUG = 2; 
    public static final int BUG_DONE = 3; // this should be last
 
    public static final int TIME_TO_MOVE_BUGS_IN_MILLISECONDS = 70; // 80 milliseconds on timer
    public static final int SECONDS_TO_CLICK = 6000; 
    public static final int NUMBER_OF_BUG_TYPES = 3;// to match the number of game levels slow + fast = 2
    public static final int MAX_NUMBER_OF_BUGS = 4; // cheap short cut for array sizing
    
    public JFrame gameJFrame;
    public Container gameContentPane;
    private int bugLevel[] = new int[MAX_NUMBER_OF_BUGS];
    private int x; 
    private boolean gameIsReady = false;
    private Bug gameBug[][] = new Bug[NUMBER_OF_BUG_TYPES][MAX_NUMBER_OF_BUGS];
    private java.util.Timer gameTimer = new java.util.Timer();
    private int xMouseOffsetToContentPaneFromJFrame = 0;
    private int yMouseOffsetToContentPaneFromJFrame = 0;
    private int miss=0;
    private int bugTimer[] = {6000}; 
    private int wins=0;
    
    public Controller(String passedInWindowTitle, 
        int gameWindowX, int gameWindowY, int gameWindowWidth, int gameWindowHeight){
        gameJFrame = new JFrame(passedInWindowTitle);
        gameJFrame.setSize(gameWindowWidth, gameWindowHeight);
        gameJFrame.setLocation(gameWindowX, gameWindowY);
        gameJFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        gameContentPane = gameJFrame.getContentPane();
        gameContentPane.setLayout(null); // not need layout, will use absolute system
        gameContentPane.setBackground(Color.white);
        gameJFrame.setVisible(true);
        // Event mouse position is given inside JFrame, where bug's image in JLabel is given inside ContentPane,
        //  so adjust for the border and frame
        int borderWidth = (gameWindowWidth - gameContentPane.getWidth())/2;  // 2 since border on either side
        xMouseOffsetToContentPaneFromJFrame = borderWidth;
        yMouseOffsetToContentPaneFromJFrame = gameWindowHeight - gameContentPane.getHeight()-borderWidth; // assume side border = bottom border; ignore title bar

        // create the bugs, now that JFrame has been initialized
        for(x=0; x<MAX_NUMBER_OF_BUGS; x++)
        {
        	gameBug[SLOW_BUG][x] = new SlowBug(gameJFrame,1,10);// JFrame, hits required,% change direction
        	gameBug[FAST_BUG][x] = new FastBug(gameJFrame,1,25);// JFrame, hits required,% change direction 
        	gameBug[VERY_FAST_BUG][x] = new veryFastBug(gameJFrame,1,30); 
        }
        
        resetGame(SLOW_BUG);
        gameTimer.schedule(this, 0, TIME_TO_MOVE_BUGS_IN_MILLISECONDS);    
 
        // register this class as a mouse event listener for the JFrame
        gameJFrame.addMouseListener(this);
    }   
    
    public void resetGame(int startingBugLevel)
    {
        gameIsReady = false;
        wins = 0;
        miss = 0;
        bugTimer[0] = 6000; 
        for(x=0; x<MAX_NUMBER_OF_BUGS; x++)
        {
        	bugLevel[x] = startingBugLevel;
        	currentBug(x).create();
        }     
        gameIsReady = true;
    }
    
    private void bugGotHit(int x)
    {
    	currentBug(x).gotHit();
    	if (currentBug(x).isDying())
    	{
   			currentBug(x).kill();   
   			bugLevel[x] ++;
            
   			if (bugLevel[x] < BUG_DONE) 
   			{ // not done, go to next level of bug
   				currentBug(x).create();
   			}
   			else
   			{
   				bugLevel[x]--; 
   				wins++; 
   			}
   		}
    }
    
    private boolean didIWin()
    {
    	if(wins == MAX_NUMBER_OF_BUGS)
    	{
    		return true;
    	}
    	else
    	{
    		return false;
    	}
    }
    
    private Bug currentBug(int x)
    {
        return gameBug[bugLevel[x]][x];
    }
    
    //run() to override run() in java.util.TimerTask
    // this is run when timer expires
    public void run() 
    {	 
        if (gameIsReady)
        for(x=0; x<MAX_NUMBER_OF_BUGS; x++)
        {       	
        	if(!currentBug(x).isDying())
        	{
        		 currentBug(x).move();
        	}     	
        }
        if (didIWin())
        {   // did they win?
           gameIsReady = false; 
           JOptionPane.showMessageDialog(gameJFrame,"You WON!");
           JOptionPane.showMessageDialog(gameJFrame,"Let's play again!");
           resetGame(SLOW_BUG);
        }
        if(bugTimer[0] <= 0)
	    { 	
        	miss++;
        	if(miss < 4)
	        {
	    		JOptionPane.showMessageDialog(gameJFrame,"You Missed");
	          	bugTimer[0] = 6000; 	           	
	        }
	    }
        else
        {
        	bugTimer[0] -= TIME_TO_MOVE_BUGS_IN_MILLISECONDS;
	    }  
        
       	if(miss == 5)
       	{
       		JOptionPane.showMessageDialog(gameJFrame,"You Missed 5 times(Loser)!");
       		for(x=0; x<MAX_NUMBER_OF_BUGS; x++)
       		{
       			currentBug(x).kill();
       		}
       		bugTimer[0] = 6000;
       		resetGame(SLOW_BUG);	           		
       	}
    }

    public void mousePressed(MouseEvent event)
    {
        // make sure game is in progress
        if (gameIsReady)
        {
        	bugTimer[0]=6000;
        	boolean ar[] = new boolean[1]; 
        	ar[0] = true; 
        	for(x = 0; x < MAX_NUMBER_OF_BUGS; x ++)
        	{ 
		            if(currentBug(x).isBugHit(event.getX()-xMouseOffsetToContentPaneFromJFrame, event.getY()-yMouseOffsetToContentPaneFromJFrame))
		            {System.out.println("I see the mouse clicked, it worked!");
		                bugGotHit(x);
		                ar[0] = false; 
		            }
        	}
		    if(ar[0] == true) // they missed so game is over
		    {
		    	miss++; 
		    	if(miss != 5)
		    	{
		      		JOptionPane.showMessageDialog(gameJFrame,"You Missed!");	
		    	}
		    }   		
        }
    }
    
    public void mouseEntered(MouseEvent event) 
    {    
    	;
    }
    public void mouseExited(MouseEvent event) 
    {
        ;
    }
    public void mouseClicked( MouseEvent event) 
    {
        ;
    }
    public void mouseReleased( MouseEvent event) 
    {
        ;
    }

    public static void main( String args[])
    {
        Controller myController = new Controller("Bug Game", 50,50, 800, 600);
        // window title, int gameWindowX, int gameWindowY, int gameWindowWidth, int gameWindowHeight){
    }
    
}