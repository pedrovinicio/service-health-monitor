import React, {useState} from "react";

import Button from "../UI/Button";
import Card from "../UI/Card";
import MessageModal from "../UI/MessageModal";
import classes from './AddService.module.css';

const AddService = props => {
    const [enteredName, setEnteredName] = useState('')
    const [enteredUrl, setEnteredUrl] = useState('')
    const [message, setMessage] = useState()

    const addServiceHandler = (event) => {
        event.preventDefault();
        if (enteredName.trim().length === 0 || enteredUrl.trim().length === 0) {
            setMessage({
                isError: true,
                title: 'Invalid input',
                message: 'Please enter a valid service name and url (non-empty values).'
            });
            return;
        }
        try {
            new URL(enteredUrl);
        } catch (err){
            setMessage({
                isError: true,
                title: 'Invalid input',
                message: 'Please enter a valid url.'
            });
            return;
        }
        props.onAddService(enteredName, enteredUrl);
        setEnteredName('');
        setEnteredUrl('');
    }

    const nameChangeHandler = (event) => {
        setEnteredName(event.target.value);
    }

    const urlChangeHandler = (event) => {
        setEnteredUrl(event.target.value);
    }

    const messageModalHandler = () => {
        setMessage(null);
    }

    return (
        <div>
            {message && <MessageModal title={message.title} message={message.message} isError={message.isError} onClose={messageModalHandler}></MessageModal>}
            <Card className={classes.input}>
                <form onSubmit={addServiceHandler}>
                    <label className={classes.title} htmlFor="title">Add service to be monitored</label>
                    <label htmlFor="name">Name</label>
                    <input id="name" type="text" value={enteredName} onChange={nameChangeHandler}></input>
                    <label htmlFor="url">Url</label>
                    <input id="url" type="text" value={enteredUrl} onChange={urlChangeHandler}></input>
                    <Button className={classes.actions} type="submit">Add</Button>
                </form>
            </Card>
        </div>
    );
};

export default AddService;