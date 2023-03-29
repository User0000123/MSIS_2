public class TableViewElement {
    public String statement;
    public int depth;

    public TableViewElement(String statement, int depth){
        this.depth = depth;
        this.statement = statement;
    }

    public int getDepth(){return this.depth;}
    public String getStatement(){return this.statement;}
}
