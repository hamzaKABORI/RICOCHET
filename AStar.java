
import javax.swing.*;
import java.awt.*;
import java.util.List;
import java.util.ArrayList;
import java.util.Collections;
import java.util.concurrent.TimeUnit;

import static java.lang.Thread.sleep;

public class AStar {
	private List<Pieces> open;
	private List<Pieces> closed;
	private List<Pieces> path;
	private Pieces now;
	private int xstart;
	private int ystart;
	private int xend, yend;
	private boolean diag;

	private boolean [][]visitedByRecur;
	private int no_of_visited_nodes;
	private Pieces [][]pcs;
	private JButton [][]Squares;
	private boolean found;
	private int move;
	AStar(JButton [][]Squares1, Pieces [][] pcs1, int xstart, int ystart, boolean diag) {
		this.open = new ArrayList<>();
		this.closed = new ArrayList<>();
		this.path = new ArrayList<>();
		move = 0;
		// this.now = pcs[ystart][xstart];
		found = false;
		this.xstart = xstart;
		this.ystart = ystart;
		this.diag = diag;
		this.pcs = pcs1;
		this.Squares = Squares1;
		no_of_visited_nodes=0;
		visitedByRecur = new boolean[Ricochet_Board.Rows][Ricochet_Board.Cols];//java init it to false
	}
	/*
      public List<Pieces> findPathTo(Pieces [][] pcs, int xend, int yend) {
          this.xend = xend;
          this.yend = yend;
          this.closed.add(this.now);
          //addNeigborsToOpenList();
          while (this.now.X != this.xend || this.now.Y != this.yend) {
              if (this.open.isEmpty()) { // Nothing to examine
                  return null;
              }
              this.now = this.open.get(0); // get first node (lowest f score)
              this.open.remove(0); // remove it
              this.closed.add(this.now); // and add to the closed
              //addNeigborsToOpenList();
          }
          this.path.add(0, this.now);
          while (this.now.X != this.xstart || this.now.Y != this.ystart) {
              this.now = this.now.parent;
              this.path.add(0, this.now);
          }
          return this.path;
      }

      private static boolean findNeighborInList(List<Pieces> array, Pieces node) {
          return array.stream().anyMatch((n) -> (n.X == node.X && n.Y == node.Y));
      }

      private double distance(int dx, int dy) {
           return Math.abs(this.now.X + dx - this.xend) + Math.abs(this.now.Y + dy - this.yend); //"Manhattan distance"
      }
    */
	 /*   private void addNeigborsToOpenList() {
	        Pieces node;
	        for (int x = -1; x <= 1; x++) {
	            for (int y = -1; y <= 1; y++) {
	            	if (!this.diag && x != 0 && y != 0) {
	                    continue; // skip if diagonal movement is not allowed
	                }
	                node = new pcs(this.now, this.now.X + x, this.now.X + y, this.now.g, this.distance(x, y));
	                if ((x != 0 || y != 0) // not this.now
	                    && this.now.x + x >= 0 && this.now.x + x < this.pcs[0].length // check pcs boundaries
	                    && this.now.y + y >= 0 && this.now.y + y < this.pcs.length
	                    && this.pcs[this.now.y + y][this.now.x + x].id != 100 // check if square is walkable
	                    && !findNeighborInList(this.open, node) && !findNeighborInList(this.closed, node)) { // if not already done
	                        Pieces.g = node.parent.g + 1.; // Horizontal/vertical cost = 1.0
	                        node.g += pcs[this.now.y + y][this.now.x + x]; // add movement cost for this square
	                        this.open.add(node);
	                }
	            }
	        }
	        Collections.sort(this.open);
	    }
	*/
	void pre_heuristic_dist(int i,int j,int prevDirection,int currentDirection) throws InterruptedException {

		TimeUnit.SECONDS.toMillis(100);
		if( !((i < Ricochet_Board.Rows && i>=0) && (j < Ricochet_Board.Cols && j>=0)) )//boundary
		{return;}

		if(no_of_visited_nodes >=252)//base condition;
		{visitedByRecur = new boolean[Ricochet_Board.Rows][Ricochet_Board.Cols];return;}

		if(currentDirection == -1) {
			pcs[i][j].h = 0;
			visitedByRecur[i][j] = true;
		}

		if(currentDirection !=-1 && pcs[i][j].h < 0) {//if new node not fuil in recursion
			return;
		}


		loop_Movement(i,j, pcs[i][j].h+1);
		visitedByRecur[i][j] = true;
		if (currentDirection != 1 && !pcs[i][j].wall_right && j+1 < Ricochet_Board.Cols && pcs[i][j+1].h>0 && !visitedByRecur[i][j+1]) {
			//visitedByRecur[i][j+1] = true;
			this.pre_heuristic_dist( i, j + 1, currentDirection, 0);
		}

		if (currentDirection != 0 && !pcs[i][j].wall_left && j-1 >=0 && pcs[i][j-1].h>0 && !visitedByRecur[i][j-1]) {
			//visitedByRecur[i][j-1] = true;
			this.pre_heuristic_dist( i, j - 1, currentDirection, 1);
		}

		if (currentDirection != 3 && !pcs[i][j].wall_bottom && i+1 < Ricochet_Board.Rows && pcs[i+1][j].h>0 && !visitedByRecur[i+1][j]) {
			//visitedByRecur[i+1][j] = true;
			this.pre_heuristic_dist(i + 1, j, currentDirection, 2);
		}

		if (currentDirection != 2 && !pcs[i][j].wall_top && i-1 >= 0 && pcs[i-1][j].h>0 && !visitedByRecur[i-1][j]) {
			//visitedByRecur[i-1][j] = true;
			this.pre_heuristic_dist(i - 1, j, currentDirection, 3);
		}
	}

	void loop_Movement(int i,int j, double CurrentHeuristic_dis){

		for(int k=j; k < Ricochet_Board.Cols; k++) {//right

			if(pcs[i][k].h<0) {//not visited
				pcs[i][k].h = CurrentHeuristic_dis;
				++no_of_visited_nodes;
			}
			else if(pcs[i][k].h>CurrentHeuristic_dis)
				pcs[i][k].h = CurrentHeuristic_dis;

			if(pcs[i][k].wall_right) break;
		}

		for(int k=j; k >= 0; k--) {//left

			if(pcs[i][k].h<0) {//not visited
				pcs[i][k].h = CurrentHeuristic_dis;
				++no_of_visited_nodes;
			}
			else if(pcs[i][k].h>CurrentHeuristic_dis)
				pcs[i][k].h = CurrentHeuristic_dis;

			if(pcs[i][k].wall_left) break; //no further moves

		}

		for(int k=i; k < Ricochet_Board.Rows; k++) {//bottom down

			if(pcs[k][j].h<0) {//not visited
				pcs[k][j].h = CurrentHeuristic_dis;
				++no_of_visited_nodes;
			}
			else if(pcs[k][j].h>CurrentHeuristic_dis)
				pcs[k][j].h = CurrentHeuristic_dis;

			if(pcs[k][j].wall_bottom) break;
		}

		for(int k=i; k>=0; k--) {//up top

			if(pcs[k][j].h<0) {//not visited
				pcs[k][j].h = CurrentHeuristic_dis;
				++no_of_visited_nodes;
			}
			else if(pcs[k][j].h>CurrentHeuristic_dis)
				pcs[k][j].h = CurrentHeuristic_dis;

			if(pcs[k][j].wall_top || pcs[k-1][j].h>0 ) break;//wall break
		}

		//print(pcs);
	}

	void find_smallest_path(int i, int j , int prevDirection, int currentDirection, double CurrentHeuristic_dis, boolean [][]visitedByRecur1){

		if(i==xend && j==yend) {
			System.out.println(visitedByRecur1[i][j]);
			System.out.printf("move = %d ", move);
			return;
		}

		visitedByRecur1[i][j] = true;
		CurrentHeuristic_dis = pcs[i][j].h;
        move++;

		int temp=-1;
        Squares[i][j].setBackground(Color.green);
		if (!found && currentDirection != 1 && !pcs[i][j].wall_right && j+1 < Ricochet_Board.Cols
				&& pcs[i][j+1].h<=CurrentHeuristic_dis && !visitedByRecur1[i][j+1]) {

			Squares[i][j+1].setBackground(Color.green);

			this.find_smallest_path(i, j + 1, currentDirection, 0, CurrentHeuristic_dis, visitedByRecur1) ;
		}

		if (!found && currentDirection != 0 && !pcs[i][j].wall_left && j-1 >=0
				&& pcs[i][j-1].h<=CurrentHeuristic_dis && !visitedByRecur1[i][j-1]) {

			Squares[i][j-1].setBackground(Color.green);
			this.find_smallest_path( i, j - 1, currentDirection, 1, CurrentHeuristic_dis,visitedByRecur1 );
		}

		if (!found && currentDirection != 3 && !pcs[i][j].wall_bottom
				&& i+1 < Ricochet_Board.Rows && pcs[i+1][j].h<=CurrentHeuristic_dis && !visitedByRecur1[i+1][j]) {

			Squares[i+1][j].setBackground(Color.green);
			this.find_smallest_path(i + 1, j, currentDirection, 2, CurrentHeuristic_dis,visitedByRecur1);
		}

		if (!found && currentDirection != 2 && !pcs[i][j].wall_top && i-1 >= 0
				&& pcs[i-1][j].h<=CurrentHeuristic_dis && !visitedByRecur1[i-1][j]) {

			Squares[i-1][j].setBackground(Color.green);
			this.find_smallest_path(i - 1, j, currentDirection, 3, CurrentHeuristic_dis ,visitedByRecur1);
		}


	}
	void print(Pieces [][]pcs) {

		for(int i=0; i<Ricochet_Board.Rows; i++) {
			for(int j=0; j<Ricochet_Board.Cols; j++) {
				if(pcs[i][j].h==-1)
					System.out.print(0);
				else System.out.print((int)pcs[i][j].h);
				System.out.print(" ");
			}
			System.out.println();
		}

		System.out.println();
	};

}
