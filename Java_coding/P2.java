public class P2{
    public static void main(String[] args){
        MatrixGraph graph = new MatrixGraph(3);
        graph.setNodeLabel(0, "A");
        graph.setNodeLabel(1, "B");
        graph.setNodeLabel(2, "C");
        graph.addEdge(0, 1);
        graph.addEdge(0, 2);
        graph.addEdge(1, 2);
        System.out.println("Graph can be colored with 2 colors: " + graph.isTwoColor());
    }

    static class MatrixGraph{
        static int RED = -1;
        static int WHITE = 1;
        static int NO_COLOR = 0;
        int[] colors; //color of the vertices
        int[][] matrix;
        String[] nodeLabels;
        int size;

        public MatrixGraph(int nodes){
            size = nodes;
            matrix = new int[size][size];
            for(int i=0; i<size;i++){
                for(int j=0; j<size;j++){
                    matrix[i][j] = 0;
                }
            }
            nodeLabels = new String[size];
            colors = new int[size];
        }

    public void setNodeLabel(int nodeIdx, String label){
        nodeLabels[nodeIdx] = label;
    }

    //create an edge
    public void addEdge(int node1, int node2){
        matrix[node1][node2] = 1;
        matrix[node2][node1] = 1; // undirected graph
    }

    public boolean DFSR(int nodeIdx, int color){
        colors[nodeIdx] = color;
        //apply the DFS process to all adjacent nodes
        for(int i=0; i<size; i++){
            if(matrix[nodeIdx][i] == 1) //check if nodeIdx is connected to i
                //vertex i is not colored yet
                if(colors[i] == NO_COLOR){
                    //color i with the opposite color of nodeIdx
                    if(!DFSR(i, -color)){
                        return false;
                    }
                }else if(colors[i] == color){
                    // vertex i is already colored with the same color as nodeIdx
                    return false;
                }
        }
        return true;
    }
    // DFS traversal
    public boolean isTwoColor(){
        //start DFS recursively from node 0
        //assume start with RED color
        return DFSR(0, RED);
    }

    }
}