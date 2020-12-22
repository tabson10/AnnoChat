package client;

public class Generate {
    public String generateName(int len){
        char[] str = new char[100];
        int r;
        for (int i = 0; i < len; i++)
        {
            r = (int)(Math.random() * 1);
            if(r==0) {
                str[i] = (char) (((int)(Math.random() * 26)) + (int)'a');
            }
            else {
                str[i] = (char) (((int)(Math.random() * 26)) + (int)'A');
            }

        }

        return (new String(str, 0, len));
    }


    public String generatePassword(int len){
        char[] str = new char[100];
        int r;
        for (int i = 0; i < len; i++)
        {
            r = (int)(Math.random() * 3);
            if(r==0) {
                str[i] = (char) (((int)(Math.random() * 26)) + (int)'a');
            }
            else if(r==1) {
                str[i] = (char) (((int)(Math.random() * 26)) + (int)'A');
            }
            else {
                str[i] = (char) (((int)(Math.random() * 10)) + (int)'0');
            }

        }

        return (new String(str, 0, len));
    }


    public String generateAddress(int len){
        char[] str = new char[len];
        for (int i = 0; i < len; i++)
        {
            str[i] = (char) (((int)(Math.random() * 10)) + (int)'0');
        }
        return (new String(str, 0, len));
    }

}
