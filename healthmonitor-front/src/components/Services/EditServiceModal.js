import React, { useState } from "react";
import Button from "../UI/Button";
import Card from "../UI/Card";
import classes from './EditServiceModal.module.css';

const EditServiceModal = props => {
    const [enteredName, setEnteredName] = useState(props.service.Name);
    const [enteredUrl, setEnteredUrl] = useState(props.service.Url);

    const nameChangeHandler = (event) => {
        setEnteredName(event.target.value);
    }

    const urlChangeHandler = (event) => {
        setEnteredUrl(event.target.value);
    }

    return (
        <div>
            <div className={classes.backdrop}></div>
            <Card className={classes.modal}>
                <header className={classes.header}>
                    <h2>Edit service</h2>
                </header>
                <div className={classes.content}>
                    <label htmlFor="name">Name</label>
                    <input id="name" type="text" value={enteredName} onChange={nameChangeHandler}></input>
                    <label htmlFor="url">Url</label>
                    <input id="url" type="text" value={enteredUrl} onChange={urlChangeHandler}></input>
                </div>
                <footer className={classes.actions}>
                    <Button onClick={props.onCancel}>Cancel</Button>
                    <Button onClick={() => {props.onSave(props.service, enteredName, enteredUrl)}}>Save</Button>
                </footer>
            </Card>     
        </div>
    );
};

export default EditServiceModal;