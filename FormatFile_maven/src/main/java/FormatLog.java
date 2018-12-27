public class FormatLog {
    public static void main(String[] args){
        if(args.length == 1) {
            ExtractRequest eRq = new ExtractRequest();
            //eRq.setAgree("1538257");
            eRq.setAgree(args[0]);
            eRq.findFile();
        }else{
            System.out.println("Должен быть один входной параметр - номер договора");
        }

    }
}
