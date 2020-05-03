import javafx.application.*;

import java.awt.*;
import java.util.Scanner;
import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.border.MatteBorder;


import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Ricochet_Board  implements ActionListener{
	private JFrame Frame;
	private JPanel panel;
	public final static int Rows = 16;//final is const in C++
	public final static int Cols = 16;
	
	private static JButton [][] Squares;
	private static Pieces [][] pcs;
	private static int Total_Chips;
	private static int current_chip_id;
	
    public static void main(String[] args)  throws Exception {
    	new Ricochet_Board();
    }
    
    public Ricochet_Board() throws IOException, InterruptedException {//GUI
	
    	Total_Chips = 17;
    	current_chip_id = 5;
    	
    	File file = new File("Ricochet_Robots.txt");
        Scanner sc = new Scanner(file);
        sc.useDelimiter("[^\\d]+");
       
           
       
        
		Frame =  new JFrame(); //window
		panel = new JPanel(); //for putting things in GUI\
		panel.setBorder(BorderFactory.createEmptyBorder(0,0,0,0));
		panel.setLayout(new GridLayout(Rows,Cols,0,0));//rows, cols, horizontal_gap, vertical_gap
		
		//CREATE EVENT HANDLERS
		Squares = new JButton[Rows][Cols];
		pcs = new Pieces[Rows][Cols];
		for(int i=0; i<Rows; i++)
			for(int j=0; j<Cols; j++) {
		      Squares[i][j] = new JButton();
		      Squares[i][j].setPreferredSize(new Dimension(50, 38));
		      Squares[i][j].addActionListener(this);
		      
		      int id = sc.nextInt();
	          int wTop = sc.nextInt();
	          int wLeft = sc.nextInt();
	          int wBottom = sc.nextInt();
	          int wRight = sc.nextInt();
	          
	          if(id==0)  pcs[i][j] = new empty((boolean)(wTop==1), (boolean)(wLeft==1), (boolean)(wBottom==1), (boolean)(wRight==1));
	          else if(id >= 5)  pcs[i][j] = new Chip(id, (boolean)(wTop==1), (boolean)(wLeft==1), (boolean)(wBottom==1), (boolean)(wRight==1));
	          else  pcs[i][j] = new Robot(id, (boolean)(wTop==1), (boolean)(wLeft==1), (boolean)(wBottom==1), (boolean)(wRight==1));
	          
	          Squares[i][j].setIcon(pcs[i][j].img);
		      Squares[i][j].setBorder(BorderFactory.createMatteBorder(1+3*wTop, 1+3*wLeft, 1+3*wBottom, 1+3*wRight, Color.black));
		      panel.add(Squares[i][j]);
		      
			}
		//BorderFactory.createMatteBorder(top, left, bottom, right, color)
		
		Squares[7][7].setBackground(Color.black);
		Squares[7][8].setBackground(Color.black);
		Squares[8][7].setBackground(Color.black);
		Squares[8][8].setBackground(Color.black);
		
		Frame.add(panel, BorderLayout.CENTER);
		Frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		Frame.setTitle("Ricochet_Robots");
		Frame.pack();
		Frame.setResizable(false);//fixed size
		Frame.setVisible(true);
		
		 sc.close();
	}

	@Override
	public void actionPerformed(ActionEvent e) {

		for(int i=0; i<Rows; i++)
			for(int j=0; j<Cols; j++)
				if(e.getSource() == Squares[i][j]) {//pressed!
					System.out.println("presssed!");
					System.out.println(pcs[i][j].id); 
					if(pcs[i][j].id >= 1 && pcs[i][j].id <= 4) {//robot pressed
						break;
					}
					
					else if(pcs[i][j].id == 0) {//no piece over there
					}
					
					else {//pressed chip pressed
						System.out.println("heuristic distance: ");
						current_chip_id  = pcs[i][j].id;
						
						ArrayList<Integer> arr = findId_XY((current_chip_id%4));
						try {
							ma(arr.get(0), arr.get(1), i,j);

						} catch (InterruptedException ex) {
							ex.printStackTrace();
						}
						/*System.out.printf("Robot Loc for this Chip: Rows(%d), Cols(%d)\n",arr.get(0), arr.get(1));
						AStar astar = new AStar(pcs, arr.get(0), arr.get(1), false) ;
						astar.print(pcs);
						*///try {
						//	astar.pre_heuristic_dist(i, j, -1,-1);//starting node..current node get zero..but nex will be one
						//} catch (InterruptedException ex) {
						//	ex.printStackTrace();
						//}
						//AStar as = new AStar(pcs, 0, 0, false);//start points
						//astar.print(pcs);
				        //List<Node> path = as.findPathTo(4, 4);
				    }
			}
	}
	
	void ma(int x, int y, int i, int j) throws InterruptedException {
		AStar as = new AStar(Squares,pcs, x, y, false);//start

		as.pre_heuristic_dist(i, j, -1,-1);//end
		as.print(pcs);
		boolean [][]visitedByRecur1 = new boolean[Ricochet_Board.Rows][Ricochet_Board.Cols];
		as.find_smallest_path(x,y, -1,-1, pcs[x][y].h, visitedByRecur1);
		System.out.println("done!");
	}
	
	ArrayList<Integer> findId_XY(int id) {
		ArrayList<Integer> arr = new ArrayList<Integer>(2);
		for(int i=0; i<Rows; i++)
			for(int j=0; j<Cols; j++)
				if(pcs[i][j].id == id) {
					arr.add(i);//x
		            arr.add(j);//y
		            return arr;
				}
		return arr;
	}
}