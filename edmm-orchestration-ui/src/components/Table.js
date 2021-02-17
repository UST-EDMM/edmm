import React from 'react';
import TableCell from '@material-ui/core/TableCell';

import TableRow from '@material-ui/core/TableRow';
import {Button} from "@material-ui/core";
import Snackbar from '@material-ui/core/Snackbar';
import { makeStyles } from '@material-ui/core/styles';
import Modal from '@material-ui/core/Modal';
import {postStartOrchestration} from "../api/TransformationFrameworkAPI";

function rand() {
    return Math.round(Math.random() * 20) - 10;
}

function getModalStyle() {
    const top = 50 + rand();
    const left = 50 + rand();

    return {
        top: `${top}%`,
        left: `${left}%`,
        transform: `translate(-${top}%, -${left}%)`,
    };
}

const useStyles = makeStyles((theme) => ({
    paper: {
        position: 'absolute',
        minWidth: "50%",
        maxHeight: "80%",
        overflowY: "auto",
        backgroundColor: theme.palette.background.paper,
        border: '2px solid #000',
        boxShadow: theme.shadows[5],
        padding: theme.spacing(2, 4, 3),
    },
}));
export default function BasicTable(props) {

    const [open, setOpen] = React.useState(false);
    const [modalOpen, setModalOpen] = React.useState(false);
    const [message, setMessage] = React.useState("");
    const [content, setContent] = React.useState("");
    const [modalStyle] = React.useState(getModalStyle);
    const classes = useStyles();

    const handleClose = () => {
        setOpen(false);
    }

    const handleModalClose = () => {
        setModalOpen(false);
    };

    const showModel = (event, key) => {
        setModalOpen(true);
        setMessage("The workflow has been uploaded to the engine!")
        setContent(props.tableValues[key].content)
        console.log(props.tableValues[key].content)
        props.tableValues[key].uploadStatus = "UPLOADED"
    };

    const startOrchestration = (event, key) => {
        setOpen(true);
        setMessage("The deployment has started!")
        props.tableValues[key].startedStatus = "STARTED"

        const endpoint = props.tableValues[key].endpoint;
        const edmmId = props.tableValues[key].edmmID;

        postStartOrchestration(endpoint, edmmId).then(r => {
            if (r.toString() === "200") {
                props.tableValues[key].startedStatus = "FINISHED"
                setOpen(true);
                setMessage("The deployment of EDMM ID: " + props.tableValues[key].edmmID + " has finished!")
            }
        })
    }

    return (
        props.tableValues && props.tableValues.map((row, i) => (
            <TableRow key={i}>
                <TableCell component="th" scope="row">
                    {row.owner}
                </TableCell>
                <TableCell align="right">{row.edmmID}</TableCell>
                <TableCell align="right">{row.startedStatus}</TableCell>
                <TableCell align="right">
                    <Button variant="outlined" onClick={(e) => showModel(e, i)} style={{color: "#0277BD"}}>Show Model</Button></TableCell>
                <TableCell align="right">
                    <Button variant="outlined"  onClick={(e) => startOrchestration(e, i)} style={{color: "#0277BD"}}>Start Orchestration</Button></TableCell>
                <Snackbar
                    anchorOrigin={{
                        vertical: 'bottom',
                        horizontal: 'left',
                    }}
                    open={open}
                    autoHideDuration={2000}
                    onClose={handleClose}
                    message= {message}
                />
                <Modal
                    open={modalOpen}
                    onClose={handleModalClose}
                    aria-labelledby="simple-modal-title"
                    aria-describedby="simple-modal-description"
                    style={{paddingTop: "15%", maxHeight: "75%"}}
                >
                    <div style={modalStyle} className={classes.paper}>
                        <p id="simple-modal-description">
                            <pre>{content}</pre>
                        </p>
                    </div>
                </Modal>
            </TableRow>
        ))
    );
}
