import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
class Memory{//Memory with 100 pages
    Page[] mem=new Page[100];

    public Memory() {//initialize
        for(int i=0;i<100;i++){
            mem[i]=new Page(-1,10,false);
        }
    }
    public int doesItExist(int pro,int page){
    	//0=go straight into memory£¬1 = already exist in the memory£¬2 = no need to trigger I/O,3 = need to trigger I/O
        for(int i=pro*4;i<pro*4+4;i++){//loop the process, load pages
            if(mem[i].ID==page){
                mem[i].ref=true;
                return 1;
            }
            if(mem[i].ID==-1){
                mem[i]=new Page(page,10,true);
                return 0;
            }
        }
        int index=-1;
        while(index==-1){
            for(int i=pro*4;i<pro*4+4;i++){
                if(!mem[i].ref){
                    index=i;
                    break;
                }else {
                    mem[i].ref=false;
                }
            }
        }
        //ReplaceÌæ»»
        if(mem[index].ID==2||mem[index].ID==5){//IO needed for 2/5
            mem[index].ID=page;
            mem[index].ref=true;
            return 3;
        }else {//IO does not needed
            mem[index].ref=true;
            mem[index].ID=page;
            return 2;
        }
    }
    public String display(){//display the memory state
        StringBuilder stringBuilder=new StringBuilder();
        for(int i=0;i<100;i++){
            stringBuilder.append(mem[i].ID+" ");
            if((i+1)%4==0){
                stringBuilder.append("||");
            }
        }
        stringBuilder.append("\n");
        return stringBuilder.toString();
    }
}
class Page{
    int ID;
    int time;
    boolean ref;

    public Page(int ID, int time, boolean ref) {
        this.ID = ID;
        this.time = time;
        this.ref = ref;
    }
}
class Process {
    int totalTime;
    int id;
    LinkedList<Page> page;//page needed for the process execution
	int readyTime;//arrival time
	int times;
    int times2;

    public Process(int id, LinkedList<Page> page, int readyTime,int times) {
        this.id = id;
        this.page = page;
        this.readyTime = readyTime;
        this.times=times;
        this.totalTime=200;
        this.times2=0;
    }
}

public class TwoCPUScheduling {
    public static void multiLevel_FB(List<Process> ready_q) {
        int t=0;//Hold the number of I/O happened
        Memory memory=new Memory();
        StringBuilder stringBuilder=new StringBuilder();
        Collections.sort(ready_q,(o1, o2) ->o2.readyTime-o1.readyTime);//sort the ready queue
		List<Process> first =new ArrayList<>();        // first level queue  (Round Robin with quantum = 8)
		List<Process> second = new ArrayList<>();      // second level queue (Round Robin with quantum = 16)
		List<Process> third = new ArrayList<>();       // third level queue  (FCFS)
		int global_time=0;//current time
		int currPro,currPage,start_time=0;//process needed, page ID, start time
		currPro=ready_q.get(13).id;//initialize to the first process id
		currPage=ready_q.get(13).page.peek().ID;//initialize to the first page ID of the first process ID
		System.out.println("process ID, page #, starting-time, leaving-time");
        while(true){//simulate the working CPU
            if(ready_q.size()>0&&ready_q.get(ready_q.size()-1).readyTime<=global_time){
                //System.out.println(ready_q.get(ready_q.size()-1).id+1);
            	//check if there is any process in the ready queue
                first.add(0,ready_q.get(ready_q.size()-1));
                ready_q.remove(ready_q.size()-1);
            }
            //check if there is any process in the first level queue
            if(first.size()>0){
                if(first.get(first.size()-1).page.peek().time==0){
                    first.get(first.size()-1).page.poll();
                }
                //check the current process page is the same as the last one
                if(currPro!=first.get(first.size()-1).id||(!first.get(first.size()-1).page.isEmpty()&&currPage!=first.get(first.size()-1).page.peek().ID)){
                    System.out.println("P"+(currPro+1)+",\t\t\tpage"+currPage+",\t\t"+start_time+"ms,\t\t"+global_time+"ms");
                  //if they are not the same£¬output (second part output)
                    currPro=first.get(first.size()-1).id;
                    currPage=first.get(first.size()-1).page.peek().ID;
                    start_time=global_time;
                }
                if(first.get(first.size()-1).page.isEmpty()){
                    first.remove(first.size()-1);
                }else {
                    first.get(first.size()-1).page.peek().time--;
                    first.get(first.size()-1).totalTime--;
                    //#Execution + 1
                    first.get(first.size()-1).times++;
                    //after 8 ms, remove it to the second level queue
                    if(first.get(first.size()-1).times==8&&first.get(first.size()-1).times2==8){
                        second.add(0,first.get(first.size()-1));
                        first.remove(first.size()-1);
                    }
                }
                //check if the page needed should be load into the memory
                int ret=memory.doesItExist(currPro,currPage);
                if(ret==0||ret==2||ret==3){
                    stringBuilder.append(memory.display());
                }
                //trigger I/O
                if(ret==3){
                    t++;
                    global_time+=30;
                    //Add back to ready queue
                    first.get(first.size()-1).times=0;
                    ready_q.add(0,first.get(first.size()-1));
                    first.remove(first.size()-1);
                }
            }else if(second.size()>0){
            	//if there is nothing in the ready queue and first level queue, execute processes in the second level queue

				//check if the page is finished
                if(second.get(second.size()-1).page.peek().time==0){
                    second.get(second.size()-1).page.poll();
                }
                //check the current process page is the same as the last one
                if(currPro!=second.get(second.size()-1).id||(!second.get(second.size()-1).page.isEmpty()&&currPage!=second.get(second.size()-1).page.peek().ID)){
                    System.out.println("P"+(currPro+1)+",\t\t\tpage"+currPage+",\t\t"+start_time+"ms,\t\t"+global_time+"ms");
                    //if they are not the same£¬output (second part output)
                    currPro=second.get(second.size()-1).id;
                    currPage=second.get(second.size()-1).page.peek().ID;
                    start_time=global_time;
                }
                //remove the page if it is finished
                if(second.get(second.size()-1).page.isEmpty()){
                    second.remove(second.size()-1);
                }else {
                    second.get(second.size()-1).totalTime--;
                    second.get(second.size()-1).page.peek().time--;
                    second.get(second.size()-1).times++;
                    //check if the second level is finished
                    if(second.get(second.size()-1).times==24&&second.get(second.size()-1).times2==24){
                        third.add(0,second.get(second.size()-1));
                        second.remove(second.size()-1);
                    }
                }
                int ret=memory.doesItExist(currPro,currPage);
                if(ret==0||ret==2||ret==3){
                    stringBuilder.append(memory.display());
                }
                if(ret==3){

                    t++;
                    global_time+=30;
                    second.get(second.size()-1).times=0;
                    ready_q.add(0,second.get(second.size()-1));
                    second.remove(second.size()-1);
                }
            }else if(third.size()>0){
                //remove the page if it is finished
                if(third.get(third.size()-1).page.peek().time==0){
                    third.get(third.size()-1).page.poll();
                }
                //check the current process page is the same as the last one
                if(currPro!=third.get(third.size()-1).id||(!third.get(third.size()-1).page.isEmpty()&&currPage!=third.get(third.size()-1).page.peek().ID)){
                    System.out.println("P"+(currPro+1)+",\t\t\tpage"+currPage+",\t\t"+start_time+"ms,\t\t"+global_time+"ms");
                    currPro=third.get(third.size()-1).id;
                    currPage=third.get(third.size()-1).page.peek().ID;
                    start_time=global_time;
                }
                if(third.get(third.size()-1).page.isEmpty()){
                    third.remove(third.size()-1);
                }else {
                    third.get(third.size()-1).totalTime--;
                    third.get(third.size()-1).page.peek().time--;
                }
                int ret=memory.doesItExist(currPro,currPage);
                if(ret==0||ret==2||ret==3){
                    stringBuilder.append(memory.display());
                }
                if(ret==3){
                    t++;
                    global_time+=30;
                    third.get(third.size()-1).times=0;
                    ready_q.add(0,third.get(third.size()-1));
                    third.remove(third.size()-1);
                }
            }
            //------------------------------------------------
            if(ready_q.size()>0&&ready_q.get(ready_q.size()-1).readyTime<=global_time){
                //check if there is any process in the ready queue
                first.add(0,ready_q.get(ready_q.size()-1));
                ready_q.remove(ready_q.size()-1);
            }

           // System.out.println(first.size());
            if(first.size()>0){
                if(first.get(first.size()-1).page.peek().time==0){
                    first.get(first.size()-1).page.poll();
                }
                //check the current process page is the same as the last one
                if(currPro!=first.get(first.size()-1).id||(!first.get(first.size()-1).page.isEmpty()&&currPage!=first.get(first.size()-1).page.peek().ID)){
                    System.out.println("P"+(currPro+1)+",\t\t\tpage"+currPage+",\t\t"+start_time+"ms,\t\t"+global_time+"ms");
                    //if they are not the same£¬output (second part output)
                    currPro=first.get(first.size()-1).id;
                    currPage=first.get(first.size()-1).page.peek().ID;
                    start_time=global_time;
                }
                if(first.get(first.size()-1).page.isEmpty()){
                    first.remove(first.size()-1);
                    global_time--;
                }else {
                    //Remaining time - 1
                    first.get(first.size()-1).page.peek().time--;
                    first.get(first.size()-1).totalTime--;
                    //#Execution + 1
                    first.get(first.size()-1).times2++;
                   // System.out.println(first.get(first.size()-1).times2+"-"+first.get(first.size()-1).times);
                    //after 8 ms, remove it to the second level queue
                    if(first.get(first.size()-1).times==8&&first.get(first.size()-1).times2==8){
                        //System.out.println((first.get(first.size()-1).id+1)+"===");
                        second.add(0,first.get(first.size()-1));
                        first.remove(first.size()-1);
                    }
                }
                
                //check if the page needed should be load into the memory
                int ret=memory.doesItExist(currPro,currPage);
                if(ret==0||ret==2||ret==3){
                    stringBuilder.append(memory.display());
                }
                //trigger I/O
                if(ret==3){
                    t++;
                    global_time+=30;
                    //added back to the ready queue
                    first.get(first.size()-1).times2=0;
                    ready_q.add(0,first.get(first.size()-1));
                    first.remove(first.size()-1);
                }
            }else if(second.size()>0){
            	//if there is nothing in the ready queue and first level queue, execute processes in the second level queue

				//check if the page is finished
                if(second.get(second.size()-1).page.peek().time==0){
                    second.get(second.size()-1).page.poll();
                }
                //check the current process page is the same as the last one
                if(currPro!=second.get(second.size()-1).id||(!second.get(second.size()-1).page.isEmpty()&&currPage!=second.get(second.size()-1).page.peek().ID)&&start_time!=global_time){
                    System.out.println("P"+(currPro+1)+",\t\t\tpage"+currPage+",\t\t"+start_time+"ms,\t\t"+global_time+"ms");
                    //if they are not the same£¬output (second part output)
                    currPro=second.get(second.size()-1).id;
                    currPage=second.get(second.size()-1).page.peek().ID;
                    start_time=global_time;
                }
                //remove the page if it is finished
                if(second.get(second.size()-1).page.isEmpty()){
                    second.remove(second.size()-1);
                }else {
                    second.get(second.size()-1).totalTime--;
                    second.get(second.size()-1).page.peek().time--;
                    second.get(second.size()-1).times2++;
                    //check if the second level is finished
                    if(second.get(second.size()-1).times==24&&second.get(second.size()-1).times2==24){
                        third.add(0,second.get(second.size()-1));
                        second.remove(second.size()-1);
                    }
                }
                int ret=memory.doesItExist(currPro,currPage);
                if(ret==0||ret==2||ret==3){
                    stringBuilder.append(memory.display());
                }
                if(ret==3){
                    t++;
                    global_time+=30;
                    second.get(second.size()-1).times2=0;
                    ready_q.add(0,second.get(second.size()-1));
                    second.remove(second.size()-1);
                }
            }else if(third.size()>0){  //FCFS
                int index=-1;
                int min=Integer.MAX_VALUE;
                for(int i=0;i<third.size();i++){
                    if(min>=third.get(i).totalTime){
                        min=third.get(i).times;
                        index=i;
                    }
                }
                //remove the page if it is finished
                if(third.get(index).page.peek().time==0){
                    third.get(index).page.poll();
                }
                if(third.get(index).page.isEmpty()){
                    third.remove(index);
                }else {
                    //check the current process page is the same as the last one
                    if(currPro!=third.get(index).id||(!third.get(index).page.isEmpty()&&currPage!=third.get(index).page.peek().ID)){
                        System.out.println("P"+(currPro+1)+",\t\t\tpage"+currPage+",\t\t"+start_time+"ms,\t\t"+global_time+"ms");
                        currPro=third.get(index).id;
                        currPage=third.get(index).page.peek().ID;
                        start_time=global_time;
                    }
                    third.get(index).totalTime--;
                    third.get(index).page.peek().time--;
                }
                int ret=memory.doesItExist(currPro,currPage);
                if(ret==0||ret==2||ret==3){
                    stringBuilder.append(memory.display());
                }
                if(ret==3){
                    t++;
                    global_time+=30;
                    third.get(index).times2=0;
                    ready_q.add(0,third.get(index));
                    third.remove(index);
                }
            }
            global_time++;
            if(ready_q.size()==0&&first.size()==0&&second.size()==0&&third.size()==0){
                System.out.println("P"+(currPro+1)+",\t\t\tpage"+currPage+",\t\t"+start_time+"ms,\t\t"+global_time+"ms");
                break;
            }

        }//while
        System.out.println(stringBuilder.toString());
        System.out.println(t);
    }

    public static void main(String args[]) throws IOException {
        List<Process> ready_q = new ArrayList<>();
        //load the input file
        BufferedReader bufferedReader=new BufferedReader(new FileReader("C:\\Users\\win\\Desktop\\processes.txt"));
        String temp=bufferedReader.readLine();
        int i=0;
        while(temp!=null){
            LinkedList<Page> page=new LinkedList<>();

            String[] split = temp.split(":")[1].split(",");
            for(int j=0;j<split.length;j++){
                if(!split[j].equals("")){
                    page.add(new Page(Integer.parseInt(split[j]),10,false));
                }
            }
            Process process = new Process(i,page,i*20,0);
            i++;
            temp=bufferedReader.readLine();
            ready_q.add(process);
        }
        multiLevel_FB(ready_q);
    }
}