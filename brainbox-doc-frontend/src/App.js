import React, { useEffect, useState } from "react";
import axios from "axios";
import "./App.css";

const API_URL = "http://localhost:8080/api/documents";

function App() {
  const [documents, setDocuments] = useState([]);
  const [form, setForm] = useState({ title: "", description: "" });
  const [file, setFile] = useState(null);
  const [editingId, setEditingId] = useState(null);
  const [error, setError] = useState("");

  useEffect(() => {
    fetchDocuments();
  }, []);

  const fetchDocuments = async () => {
    const res = await axios.get(API_URL);
    setDocuments(res.data);
  };

  const handleChange = (e) => {
    setForm({ ...form, [e.target.name]: e.target.value });
    setError("");
  };

  const handleFileChange = (e) => {
    setFile(e.target.files[0]);
    setError("");
  };

  const handleSubmit = async (e) => {
    e.preventDefault();
    if (!form.title.trim() || !form.description.trim()) {
      setError("Both title and description are required.");
      return;
    }
    if (!file && !editingId) {
      setError("File is required for new documents.");
      return;
    }
    try {
      const formData = new FormData();
      formData.append("title", form.title);
      formData.append("description", form.description);
      if (file) formData.append("file", file);

      if (editingId) {
        await axios.put(`${API_URL}/${editingId}`, formData, {
          headers: { "Content-Type": "multipart/form-data" },
        });
        setEditingId(null);
      } else {
        await axios.post(API_URL, formData, {
          headers: { "Content-Type": "multipart/form-data" },
        });
      }
      setForm({ title: "", description: "" });
      setFile(null);
      fetchDocuments();
    } catch (err) {
      setError("Something went wrong. Please try again.");
    }
  };

  const handleEdit = (doc) => {
    setForm({ title: doc.title, description: doc.description });
    setEditingId(doc.id);
    setFile(null);
    setError("");
  };

  const handleDelete = async (id) => {
    await axios.delete(`${API_URL}/${id}`);
    fetchDocuments();
  };

  return (
    <div className="app-container">
      <h1>BrainBox Documents</h1>
      <form onSubmit={handleSubmit} encType="multipart/form-data">
        <input
          name="title"
          placeholder="Title"
          value={form.title}
          onChange={handleChange}
          maxLength={50}
          required
        />
        <input
          name="description"
          placeholder="Description"
          value={form.description}
          onChange={handleChange}
          maxLength={120}
          required
        />
        <input
          type="file"
          name="file"
          onChange={handleFileChange}
          accept=".pdf,.doc,.docx,.txt"
          required={!editingId}
        />
        <button type="submit">{editingId ? "Update" : "Add"}</button>
        {editingId && (
          <button
            type="button"
            onClick={() => {
              setEditingId(null);
              setForm({ title: "", description: "" });
              setFile(null);
              setError("");
            }}
          >
            Cancel
          </button>
        )}
      </form>
      {error && (
        <div style={{ color: "#dc2626", marginBottom: 12, textAlign: "center" }}>
          {error}
        </div>
      )}
      <ul>
        {documents.map((doc) => (
          <li key={doc.id}>
            <span>
              <b>{doc.title}</b>: {doc.description}
            </span>
            <button onClick={() => handleEdit(doc)}>Edit</button>
            <button onClick={() => handleDelete(doc.id)}>Delete</button>
          </li>
        ))}
      </ul>
    </div>
  );
}

export default App;
