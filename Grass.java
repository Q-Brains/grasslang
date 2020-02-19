import java.io.*;
import java.util.*;


public class Grass {
    public static void main(String[] args) {
        if (args.length < 1) {
            System.err.println("Usage: java Grass file");
            return;
        }

        try {
            Grass grass = new Grass(args[0]);
            grass.run();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        System.out.println();
    }

    private Code code;

    public Grass(String fileName) throws FileNotFoundException, IOException {
        FileReader fileReader = new FileReader(fileName);
        try {
            BufferedReader buffReader = new BufferedReader(fileReader);
            try {
                Parser parser = new Parser();
                code = parser.parse(buffReader);
            } finally {
                buffReader.close();
            }
        } finally {
            fileReader.close();
        }
    }

    public void run() {
        int i = 1;
        CED ced = new CED(code);
        while(true) {
            Insn insn = ced.popCode();
            if(insn == null) {
                if(!ced.restoreDump()){
                    break;
                }
            } else {
                insn.eval(ced);
            }
        }
        System.out.flush();
    }
}


class Parser {
    private List<Insn> code;
    
    enum Token {
        LCW,
        UCW,
        V,
        EOF
    }

    public Token read(Reader reader) throws IOException {
        int i;
        while((i = reader.read()) != -1) {
            char ch = (char)i;
            switch(ch) {
              case 'w'  :
              case 'ｗ' :
                return Token.LCW;
                
              case 'W'  :
              case 'Ｗ' :
                return Token.UCW;
                
              case 'v'  :
              case 'ｖ' :
                return Token.V;
            }
        }

        return Token.EOF;
    }

    public Code parse(Reader reader) throws IOException {
        Code code = new Code();;
        Token token;

        while((token = read(reader)) != Token.EOF) {
            if(token == Token.LCW) {
                parseAbs(code, reader);
                parseProg(code, reader);
                break;
            }
        }

        return code;
    }

    private void parseProg(Code code, Reader reader) throws IOException {
        while(true) {
            Token token = read(reader);
            switch(token) {
              case LCW:
                parseAbs(code, reader);
                continue;

              case UCW:
                parseAppList(code, reader);
                continue;
            }
            break;
        }
    }

    private void parseAbs(Code code, Reader reader) throws IOException {
        Code body = new Code();
        int argNum = 1;
        
        while(true) {
            Token token = read(reader);
            switch(token) {
              case LCW:
                argNum++;
                continue;

              case UCW:
                parseAppList(body, reader); 
                break;
            }
            break;
        }

        code.add(new Abs(argNum,body));
        return;
    }

    private void parseAppList(Code code, Reader reader) throws IOException {
        int fun = 1;
        int arg = 0;

        while(true) {
            Token token = read(reader);
            switch(token) {
              case EOF:
              case V:
                if(arg == 0) {
                    new RuntimeException("parse error.");
                }
                code.add(new App(fun, arg));
                break;

              case UCW:
                if(arg == 0) {
                    fun++;
                } else {
                    code.add(new App(fun, arg));
                    fun = 1;
                    arg = 0;
                }
                continue;

              case LCW:
                arg++;
                continue;
            }
            break;
        }

        return;
    }
}



class Code  {
    private final List<Insn> list = new LinkedList<Insn>();
    
    public void add(Insn insn){
        list.add(insn);
    }
    
    public Insn poll(){
        if(list.isEmpty()) {
            return null;
        } else {
            return list.remove(0);
        }
    }

    public Code clone() {
        Code code = new Code();
        code.list.addAll(list);
        return code;
    }

    public String toString() {
        return list.toString();
    }
}

class Env {
    private final List<Value> list = new LinkedList<Value>();

    public void push(Value value) {
        list.add(0, value);
    }

    public Value get(int i) {
        if(list.isEmpty()) {
            return null;
        } else {
            return list.get(i);
        }
    }

    public Env clone() {
        Env env = new Env();
        env.list.addAll(list);
        return env;
    }
    
    public String toString() {
        return list.toString();
    }
}



interface Insn {
    void eval(CED ced);
}


class App implements Insn {
    private int fun;
    private int arg;

    App(int fun, int arg) {
        this.fun = fun;
        this.arg = arg;
    }

    public void eval(CED ced) {
        Value funVal = ced.getEnvValue(fun-1);
        Value argVal = ced.getEnvValue(arg-1);
        funVal.apply(ced, argVal);
    }

    public String toString() {
        return "App(" + fun + ", " + arg + ")";
    }
}

class Abs implements Insn {
    private int argNum;
    private Code body;

    Abs(int argNum, Code body) {
        this.argNum = argNum;
        this.body = body;
    }

    public void eval(CED ced) {
        if(argNum == 1) {
            ced.pushEnvCode(body);
        } else {
            Abs abs = new Abs(argNum-1, body.clone());
            Code code = new Code();
            code.add(abs);
            ced.pushEnvCode(code);
        }
    }

    public String toString() {
        return "Abs(" + argNum + ", " + body + ")";
    }
}


abstract class Value {
    public char getCh() { throw new RuntimeException("getCh"); }
    abstract public void apply(CED ced, Value argVal);
}


class CE extends Value {
    static final CE TRUE;
    static final CE FALSE;

    static {
        Code tCodeAbsCode = new Code();
        tCodeAbsCode.add(new App(2,3));
        Code tCode = new Code();
        tCode.add(new Abs(1, tCodeAbsCode));
        Env tEnv = new Env();
        tEnv.push(new CE(new Code(), new Env()));
        TRUE = new CE(tCode, tEnv);

        Code fCode = new Code();
        fCode.add(new Abs(1, new Code()));
        Env fEnv = new Env();
        FALSE = new CE(fCode, fEnv);
    }
    
    
    private Code code;
    private Env env;

    CE(Code code, Env env) {
        this.code = code;
        this.env = env;
    }

    public Code getCode() {
        return code;
    }

    public Env getEnv() {
        return env;
    }

    public void apply(CED ced, Value argVal) {
        ced.saveDump(this);
        ced.pushEnv(argVal);
    }

    public String toString() {
        return "{code: " + code + "}";
    }
}


class CharFn extends Value {
    
    private char ch;
    
    public CharFn(char ch) {
        this.ch = ch;
    }

    public char getCh() { return ch; }

    public void apply(CED ced, Value argVal) {
        if(equals(argVal)) {
            ced.pushEnv(CE.TRUE);
        } else {
            ced.pushEnv(CE.FALSE);
        }
    }

    public boolean equals(Object o) {
        if(o != null) { return false; }
        if(!(o instanceof CharFn)) { return false; }
        
        CharFn chApp = (CharFn)o;
        if(ch != chApp.ch) { return false; }

        return true;
    }
    
    public String toString() {
        return "'" + ch + "'";
    }
}


class Out extends Value {
    public void apply(CED ced, Value argVal) {
        System.out.write((int)argVal.getCh());
        ced.pushEnv(argVal);
    }

    public String toString() {
        return "OUT";
    }
}


class In extends Value {
    public void apply(CED ced, Value argVal) {
        try {
            int ch = System.in.read();
            if(ch != -1) {
                CharFn chApp = new CharFn((char)ch);
                ced.pushEnv(chApp);
            } else {
                ced.pushEnv(argVal);
            }
        } catch(IOException ex) {
            ex.printStackTrace();
            throw new RuntimeException("Input Error.");
        }
    }

    public String toString() {
        return "In";
    }
}

class Succ extends Value {
    public void apply(CED ced, Value argVal) {
        char ch = argVal.getCh();
        ch++;
        if(255 < ch) { ch = 0; }
        CharFn chApp = new CharFn(ch);
        ced.pushEnv(chApp);
    }

    public String toString() {
        return "Succ";
    }
}


class Dump {
    private final List<CE> list = new LinkedList<CE>();

    public void push(CE ce) {
        list.add(0, ce);
    }

    public CE pop() {
        if(list.isEmpty()) {
            return null;
        } else {
            return list.remove(0);
        }
    }

    public String toString() {
        return list.toString();
    }
}



class CED {
    private Code code;
    private Env env;
    private Dump dump;

    CED(Code code) {
        this.code = code.clone();

        env = new Env();
        env.push(new In());
        env.push(new CharFn('w'));
        env.push(new Succ());
        env.push(new Out());

        dump = new Dump();
        Code dumpCode = new Code();
        dumpCode.add(new App(1,1));
        Env dumpEnv = new Env();
        dump.push(new CE(dumpCode,dumpEnv));
    }

    public void saveDump(CE newCE) {
        CE dumpCE = new CE(code, env);
        dump.push(dumpCE);
        code = newCE.getCode().clone();
        env = newCE.getEnv().clone();
    }

    public boolean restoreDump() {
        CE dumpCE = dump.pop();
        if(dumpCE == null) { return false; }

        code = dumpCE.getCode();
        Value f = getEnvValue(0);
        env = dumpCE.getEnv();
        pushEnv(f);

        return true;
    }
    
    public Value getEnvValue(int index) {
        return env.get(index);
    }

    public void pushEnv(Value val) {
        env.push(val);
    }

    public void pushEnvCode(Code code) {
        CE ce = new CE(code, env.clone());
        pushEnv(ce);
    }

    public Insn popCode() {
        return code.poll();
    }

    public String toString() {
        return "[code: " + code
          + ", env: " + env
          + ", dump: " + dump + "]";
    }
}
