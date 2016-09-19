import javax.swing.ImageIcon;
import javax.swing.JFrame;

public class veryFastBug extends Bug
{
	public veryFastBug(JFrame myJFrame, int hitsNeeded, int directionChangeProbability)
	{
		super(myJFrame, hitsNeeded, directionChangeProbability);//call Bug constructor
		
		//set up images for very fast bug
		bugImageName[UP] = "Ant_Up.jpg";
		bugImageName[DOWN] = "Ant_Down.jpg"; 
		bugImageName[LEFT] = "Ant_Left.jpg"; 
		bugImageName[RIGHT] = "Ant_Right.jpg"; 
		
		for (int i = UP; i <= RIGHT; i++)
		{// up =0, right = 3
            bugImage[i] = new ImageIcon (bugImageName[i]);
        }
        
        // movement is arbitrarily based on size of image
        horizontalMovement = bugImage[RIGHT].getIconWidth()/3; // arbitrary 1/3 of width
        verticalMovement = bugImage[UP].getIconHeight()/3; // arbitrary 1/3 of height;     
	}
	 
	public void move()
	{
        // change direction?
        if (Math.random()*100 < directionChangeProbability) // Math.random gives 0 to .9999
            bugDirection = (int) Math.floor(Math.random()*NUMBER_OF_DIRECTIONS); 
        
        if (bugDirection == LEFT)
            xPosition -= horizontalMovement;
        else if (bugDirection == RIGHT)
            xPosition += horizontalMovement;            
        else if (bugDirection == UP)
            yPosition -= verticalMovement;
        else if (bugDirection == DOWN)
            yPosition += verticalMovement;
        
        drawBug();          
        
        //hit edge of window and need to turn around?
        if (bugDirection == UP && atTopEdge())
        {
            bugDirection = DOWN;    
        }
        else if (bugDirection == DOWN && atBottomEdge())
        {
            bugDirection = UP;  
        }
        else if (bugDirection == LEFT && atLeftEdge())
        {
            bugDirection = RIGHT;   
        }
        else if (bugDirection == RIGHT && atRightEdge())
        {
            bugDirection = LEFT;    
        }
	}
}
