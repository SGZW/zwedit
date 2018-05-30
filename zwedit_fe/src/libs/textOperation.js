class TextOperation {
    
    constructor() {
        this.ops = [];
        this.baseLength = 0;
        this.targetLength = 0;
    }
    
    equals(other) {
        if (this.baseLength !== other.baseLength) { return false; }
        if (this.targetLength !== other.targetLength) { return false; }
        if (this.ops.length !== other.ops.length) { return false; }
        for (let i = 0; i < this.ops.length; i++) {
            if (this.ops[i] !== other.ops[i]) { return false; }
        }
        return true;
    }
    
    static isRetain(op) {
        return typeof op === 'number' && op > 0;
    }
    
    static isInsert(op) {
        return typeof op === 'string';
    }
    
    static isDelete(op) {
        return typeof op === 'number' && op < 0;
    }
    
    retain(n) {
        if (typeof n !== 'number') {
            throw new Error("retain expects an integer");
        }
        if (n === 0) { return this; }
        this.baseLength += n;
        this.targetLength += n;
        if (isRetain(this.ops[this.ops.length-1])) {
            this.ops[this.ops.length-1] += n;
        } else {
            this.ops.push(n);
        }
        return this;
    }
    
    insert(str) {
        if (typeof str !== 'string') {
            throw new Error("insert expects a string");
        }
        if (str === '') { return this; }
        this.targetLength += str.length;
        var ops = this.ops;
        if (isInsert(ops[ops.length-1])) {
            ops[ops.length-1] += str;
        } else if (isDelete(ops[ops.length-1])) {
            if (isInsert(ops[ops.length-2])) {
                ops[ops.length-2] += str;
            } else {
                ops[ops.length] = ops[ops.length-1];
                ops[ops.length-2] = str;
            }
        } else {
            ops.push(str);
        }
        return this;
    }

    delete(n) {
        if (typeof n === 'string') { n = n.length; }
        if (typeof n !== 'number') {
            throw new Error("delete expects an integer or a string");
        }
        if (n === 0) { return this; }
        if (n > 0) { n = -n; }
        this.baseLength -= n;
        if (isDelete(this.ops[this.ops.length-1])) {
            this.ops[this.ops.length-1] += n;
        } else {
            this.ops.push(n);
        }
        return this;
    }
        
    toString() {
        var map = Array.prototype.map || function (fn) {
            var arr = this;
            var newArr = [];
            for (var i = 0, l = arr.length; i < l; i++) {
                newArr[i] = fn(arr[i]);
            }
            return newArr;
        };
        return map.call(this.ops, function (op) {
            if (isRetain(op)) {
                return "retain " + op;
            } else if (isInsert(op)) {
                return "insert '" + op + "'";
            } else {
                return "delete " + (-op);
            }
        }).join(', ');
    }
    
    toJSON() {
        return this.ops;
    }
    
    static fromJSON(ops) {
        var o = new TextOperation();
        for (let i = 0, l = ops.length; i < l; i++) {
            var op = ops[i];
            if (isRetain(op)) {
                o.retain(op);
            } else if (isInsert(op)) {
                o.insert(op);
            } else if (isDelete(op)) {
                o.delete(op);
            } else {
                throw new Error("unknown operation: " + JSON.stringify(op));
            }
        }
        return o;
    }
    
    apply(str) {
        if (str.length !== this.baseLength) {
            throw new Error("The operation's base length must be equal to the string's length.");
        }
        var newStr = [], j = 0;
        var strIndex = 0;
        for (let i = 0; i < this.ops.length; i++) {
            var op = this.ops[i];
            if (isRetain(op)) {
                if (strIndex + op > str.length) {
                    throw new Error("Operation can't retain more characters than are left in the string.");
                }
                newStr[j++] = str.slice(strIndex, strIndex + op);
                strIndex += op;
            } else if (isInsert(op)) {
                newStr[j++] = op;
            } else {
                strIndex -= op;
            }
        }
        if (strIndex !== str.length) {
            throw new Error("The operation didn't operate on the whole string.");
        }
        return newStr.join('');
    }
    
    invert() {
        var strIndex = 0;
        var inverse = new TextOperation();
        var ops = this.ops;
        for (var i = 0, l = ops.length; i < l; i++) {
            var op = ops[i];
            if (isRetain(op)) {
                inverse.retain(op);
                strIndex += op;
            } else if (isInsert(op)) {
                inverse.delete(op.length);
            } else { 
                inverse.insert(str.slice(strIndex, strIndex - op));
                strIndex -= op;
            }
        }
        return inverse;
    }
        
    compose(operation2) {
        var operation1 = this;
        if (operation1.targetLength !== operation2.baseLength) {
            throw new Error("The base length of the second operation has to be the target length of the first operation");
        }

        var operation = new TextOperation();
        var ops1 = operation1.ops, ops2 = operation2.ops;
        var i1 = 0, i2 = 0;
        var op1 = ops1[i1++], op2 = ops2[i2++];
        while (true) {
            if (typeof op1 === 'undefined' && typeof op2 === 'undefined') {
                break;
            }

            if (isDelete(op1)) {
                operation.delete(op1);
                op1 = ops1[i1++];
                continue;
            }
            if (isInsert(op2)) {
                operation.insert(op2);
                op2 = ops2[i2++];
                continue;
            }

            if (typeof op1 === 'undefined') {
                throw new Error("Cannot compose operations: first operation is too short.");
            }
            if (typeof op2 === 'undefined') {
                throw new Error("Cannot compose operations: first operation is too long.");
            }

            if (isRetain(op1) && isRetain(op2)) {
                if (op1 > op2) {
                    operation.retain(op2);
                    op1 = op1 - op2;
                    op2 = ops2[i2++];
                } else if (op1 === op2) {
                    operation.retain(op1);
                    op1 = ops1[i1++];
                    op2 = ops2[i2++];
                } else {
                    operation.retain(op1);
                    op2 = op2 - op1;
                    op1 = ops1[i1++];
                }
            } else if (isInsert(op1) && isDelete(op2)) {
                if (op1.length > -op2) {
                    op1 = op1.slice(-op2);
                    op2 = ops2[i2++];
                } else if (op1.length === -op2) {
                    op1 = ops1[i1++];
                    op2 = ops2[i2++];
                } else {
                    op2 = op2 + op1.length;
                    op1 = ops1[i1++];
                }
            } else if (isInsert(op1) && isRetain(op2)) {
                if (op1.length > op2) {
                    operation.insert(op1.slice(0, op2));
                    op1 = op1.slice(op2);
                    op2 = ops2[i2++];
                } else if (op1.length === op2) {
                    operation.insert(op1);
                    op1 = ops1[i1++];
                    op2 = ops2[i2++];
                } else {
                    operation.insert(op1);
                    op2 = op2 - op1.length;
                    op1 = ops1[i1++];
                }
            } else if (isRetain(op1) && isDelete(op2)) {
                if (op1 > -op2) {
                    operation['delete'](op2);
                    op1 = op1 + op2;
                    op2 = ops2[i2++];
                } else if (op1 === -op2) {
                    operation['delete'](op2);
                    op1 = ops1[i1++];
                    op2 = ops2[i2++];
                } else {
                    operation['delete'](op1);
                    op2 = op2 + op1;
                    op1 = ops1[i1++];
                }
            } else {
                throw new Error(
                    "This shouldn't happen: op1: " +
                    JSON.stringify(op1) + ", op2: " +
                    JSON.stringify(op2)
                );
            }
        }
        return operation;
    }
    
    static getSimpleOp (operation) {
        var ops = operation.ops;
        var isRetain = TextOperation.isRetain;
        switch (ops.length) {
            case 1:
                return ops[0];
            case 2:
                return isRetain(ops[0]) ? ops[1] : (isRetain(ops[1]) ? ops[0] : null);
            case 3:
                if (isRetain(ops[0]) && isRetain(ops[2])) { return ops[1]; }
        }
        return null;
    }

    static getStartIndex (operation) {
        if (isRetain(operation.ops[0])) { return operation.ops[0]; }
        return 0;
    }
    
    shouldBeComposedWith(other) {
        if (this.isNoop() || other.isNoop()) { return true; }
        var startA = getStartIndex(this), startB = getStartIndex(other);
        var simpleA = getSimpleOp(this), simpleB = getSimpleOp(other);
        if (!simpleA || !simpleB) { return false; }
        if (isInsert(simpleA) && isInsert(simpleB)) {
            return startA + simpleA.length === startB;
        }
        if (isDelete(simpleA) && isDelete(simpleB)) {
            return (startB - simpleB === startA) || startA === startB;
        }
        return false;
    }

    shouldBeComposedWithInverted(other) {
        if (this.isNoop() || other.isNoop()) { return true; }
        var startA = getStartIndex(this), startB = getStartIndex(other);
        var simpleA = getSimpleOp(this), simpleB = getSimpleOp(other);
        if (!simpleA || !simpleB) { return false; }

        if (isInsert(simpleA) && isInsert(simpleB)) {
            return startA + simpleA.length === startB || startA === startB;
        }

        if (isDelete(simpleA) && isDelete(simpleB)) {
            return startB - simpleB === startA;
        }

        return false;
    }
    
    static transform(operation1, operation2) {
        if (operation1.baseLength !== operation2.baseLength) {
            throw new Error("Both operations have to have the same base length");
        }

        var operation1prime = new TextOperation();
        var operation2prime = new TextOperation();
        var ops1 = operation1.ops, ops2 = operation2.ops;
        var i1 = 0, i2 = 0;
        var op1 = ops1[i1++], op2 = ops2[i2++];
        while (true) {
            if (typeof op1 === 'undefined' && typeof op2 === 'undefined') {
                break;
            }

            if (isInsert(op1)) {
                operation1prime.insert(op1);
                operation2prime.retain(op1.length);
                op1 = ops1[i1++];
                continue;
            }
            if (isInsert(op2)) {
                operation1prime.retain(op2.length);
                operation2prime.insert(op2);
                op2 = ops2[i2++];
                continue;
            }

            if (typeof op1 === 'undefined') {
                throw new Error("Cannot compose operations: first operation is too short.");
            }
            if (typeof op2 === 'undefined') {
                throw new Error("Cannot compose operations: first operation is too long.");
            }

            var minl;
            if (isRetain(op1) && isRetain(op2)) {
                if (op1 > op2) {
                    minl = op2;
                    op1 = op1 - op2;
                    op2 = ops2[i2++];
                } else if (op1 === op2) {
                    minl = op2;
                    op1 = ops1[i1++];
                    op2 = ops2[i2++];
                } else {
                    minl = op1;
                    op2 = op2 - op1;
                    op1 = ops1[i1++];
                }
                operation1prime.retain(minl);
                operation2prime.retain(minl);
            } else if (isDelete(op1) && isDelete(op2)) {
                if (-op1 > -op2) {
                    op1 = op1 - op2;
                    op2 = ops2[i2++];
                } else if (op1 === op2) {
                    op1 = ops1[i1++];
                    op2 = ops2[i2++];
                } else {
                    op2 = op2 - op1;
                    op1 = ops1[i1++];
                }
            } else if (isDelete(op1) && isRetain(op2)) {
                if (-op1 > op2) {
                    minl = op2;
                    op1 = op1 + op2;
                    op2 = ops2[i2++];
                } else if (-op1 === op2) {
                    minl = op2;
                    op1 = ops1[i1++];
                    op2 = ops2[i2++];
                } else {
                    minl = -op1;
                    op2 = op2 + op1;
                    op1 = ops1[i1++];
                }
                operation1prime.delete(minl);
            } else if (isRetain(op1) && isDelete(op2)) {
                if (op1 > -op2) {
                    minl = -op2;
                    op1 = op1 + op2;
                    op2 = ops2[i2++];
                } else if (op1 === -op2) {
                    minl = op1;
                    op1 = ops1[i1++];
                    op2 = ops2[i2++];
                } else {
                    minl = op1;
                    op2 = op2 + op1;
                    op1 = ops1[i1++];
                }
                operation2prime.delete(minl);
            } else {
                throw new Error("The two operations aren't compatible");
            }
        }

        return [operation1prime, operation2prime];
    }
}

export default TextOperation;
