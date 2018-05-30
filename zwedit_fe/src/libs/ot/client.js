import Syschronized from '@/libs/ot/syschronized';
import CodemirrorAdapter from '@/libs/ot/codemirrorAdapter';

class Client {

    socket;
        
    cm;
    
    revision;

    state;
 
    constructor(socket, cm, revision) {
        this.socket = socket;
        this.cm = cm;
        this.revision = revision;
        this.state = new Syschronized();
    }
    
    applyOperation(operation) {
        CodemirrorAdapter.applyOperationToCodeMirror(this.cm, operation); 
    }
    
    sendOperation(revision, operation) {
        if(this.socket.readyState === WebSocket.OPEN) {
            var req = {
                "revision": revision,
                "actions": operation.toJSON(),
            }
            console.log("req");
            console.log(req);
            this.socket.send(JSON.stringify(req));
        }
    }
    
    setState(state) {
        this.state = state;
    }
    
    applyClient(operation) {
        this.setState(this.state.applyClient(this, operation));       
    }
    
    applyServer(operation) {
        this.revision++;
        this.setState(this.state.applyServer(this, operation));
    }
    
    serverAck() {
        this.revision++;
        this.setState(this.state.serverAck(this));
    }
}

export default Client;
