import AwaitingWithBuffer from '@/libs/ot/awaitingWithBuffer';
import TextOperation from '@/libs/ot/textOperation';
import Synchonized from '@/libs/ot/syschronized';

class AwaitingConfirm {
    
    outstanding;
    
    constructor(outstanding) {
        this.outstanding = outstanding;
    }   
 
    applyClient(client, operation) {
        return new AwaitingWithBuffer(this.outstanding, operation);
    }
    
    applyServer(client, operation) {
        var pair = TextOperation.transform(this.outstanding, operation);
        client.applyOperation(pair[1]);
        return new AwaitingConfirm(pair[0]);
    }

    serverAck(client) {
        return new Synchonized();
    }
}

export default AwaitingConfirm;
