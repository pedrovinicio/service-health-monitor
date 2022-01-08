import React from "react";
import Card from "./Card";
import Button from "./Button";
import classes from './ConfirmationModal.module.css'

const ConfirmationModal = props => {
    return (
        <div>
            <div className={classes.backdrop}></div>
            <Card className={classes.modal}>
                <header className={classes.header}>
                    <h2>{props.title}</h2>
                </header>
                <div className={classes.content}>
                    <p>{props.message}</p>
                </div>
                <footer className={classes.actions}>
                    <Button className={classes.buttonCancel} onClick={props.onCancel}>Cancel</Button>
                    <Button className={classes.buttonConfirm} onClick={() => {props.onConfirm(props.service)}}>Confirm</Button>
                </footer>
            </Card>     
        </div>
    );
};

export default ConfirmationModal;