import React from "react";
import Card from "./Card";
import Button from "./Button";
import classes from './MessageModal.module.css'

const MessageModal = props => {
    return (
        <div>
            <div className={classes.backdrop} onClick={props.onClose}></div>
            <Card className={classes.modal}>
                <header className={props.isError ? classes.headerFailure : classes.headerSuccess}>
                    <h2>{props.title}</h2>
                </header>
                <div className={classes.content}>
                    <p>{props.message}</p>
                </div>
                <footer className={props.isError ? classes.actionsFailure : classes.actions}>
                    <Button onClick={props.onClose}>Okay</Button>
                </footer>
            </Card>     
        </div>
    );
};

export default MessageModal;